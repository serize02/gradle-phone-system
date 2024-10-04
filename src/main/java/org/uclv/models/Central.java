package org.uclv.models;

import org.uclv.exceptions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Central {
    private String name;
    // TODO Check whether the name is valid or not
    private String address;
    private List<Client> clients = new ArrayList<>();

    public List<Call> getCalls() {
        return calls_history;
    }

    private List<Call> calls_history;

    public List<Tax> getTaxes() {
        return taxes;
    }

    private List<Tax> taxes;

    public Central(String name, String address, ArrayList<Client> clients, ArrayList<Call> calls_history, ArrayList<Tax> taxes) {
        this.name = name;
        this.address = address;
        this.clients = clients;
        this.calls_history = calls_history;
        this.taxes = taxes;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Returns an ordered ArrayList of the Central's clients
     */
    public List<Client> getClients() {

        return clients;
    }

    public Client verifyClient(String code) throws InvalidCredentialsE {
        for (Client client : clients) {
            if (client.getCode().equals(code)) {
                return client;
            }
        }
        throw new InvalidCredentialsE();
    }

    public Client getClientByCode(String code) {
        for (Client client : clients) {
            if (client.getCode().equals(code)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Adds a client to the Central's client list and keeps it ordered
     *
     * @param client The client to be added
     */
    public void addClient(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
            clients.sort(new Comparator<Client>() {
                @Override
                public int compare(Client o1, Client o2) {
                    return o1.getCode().compareTo(o2.getCode());
                }
            });
        }
    }

    //TODO E
    public void showClients() throws WrongCodeFormatE {
        for (Client client : clients) {
            System.out.println(client.getCode());
        }
        System.out.println("------");
    }

    public void addCall(Call call) {

        calls_history.add(call);
    }

    public void addTax(Tax tax) throws TaxAlreadyExists {
        if (!taxes.contains(tax)) {
            taxes.add(tax);
        } else throw new TaxAlreadyExists();
    }

    private boolean checkPeriod(String period) {
        String[] months = period.split("-");

        if (months.length != 2)
            return true;

        int start, end;

        try {
            start = Integer.parseInt(months[0]);
            end = Integer.parseInt(months[1]);
        } catch (NumberFormatException e) {
            return true;
        }

        return (start < 1 || end < 1 || end > 12 || start > end);


    }

    /**
     * Returns the cost of the given call
     *
     * @param call The call we want to know the cost of
     */
    private float getCallCost(Call call) {
        String receiver_country_code = call.getReceiverCountryCode();
        String sender_country_code = call.getSenderCountryCode();
        String receiver_location_code = call.getReceiverLocationCode();
        String sender_location_code = call.getSenderLocationCode();
        float cost = 0;

        // If receiver and sender codes are equal, it's a national call
        if (receiver_country_code.equals(sender_country_code)) {
            cost = Math.abs(Integer.parseInt(receiver_location_code) - Integer.parseInt(sender_location_code));

            // If location codes are equal, it's a call in the same location
            if (cost == 0) {
                cost = (float) call.getTime() / 60;
            } else {
                cost *= (float) call.getTime() / 60;
            }
        } else {
            String country_code;
            String location_code;

            // As the central is in Cuba, either the sender or the receiver must be cuban. Get the one that's not
            if (!receiver_country_code.equals("+53")) {
                country_code = receiver_country_code;
                location_code = receiver_location_code;
            } else {
                country_code = sender_country_code;
                location_code = sender_location_code;
            }

            int i = 0;

            // Search for the tax that matches country and location codes (It's guaranteed that exists)
           while (i < taxes.size() &&  !taxes.get(i).getCountryCode().equals(country_code)) {
                i++;
            }

            // As time is in seconds, convert to minutes
            cost = (float) call.getTime() / 60;
            cost *= taxes.get(i).getValue();
        }

        // Round the cost to 2 decimals
        BigDecimal rounded_cost = new BigDecimal(cost);
        rounded_cost = rounded_cost.setScale(2, RoundingMode.HALF_UP);
        return rounded_cost.floatValue();
    }

    /**
     * Returns earnings matching the specified {@code operation} and {@code month}
     *
     * @param operation An integer between 1 and 3, each one standing for:
     *                  <ol>
     *                   <li>National Calls</li>
     *                   <li>International Calls</li>
     *                   <li>Total</li>
     *                  </ol>
     * @param month     An integer between 1 and 12 representing each month
     */
    // TODO Error to check the operation is an integer between 1 and 3 *************
    // TODO Error to check the month is an integer between 1 and 12 ***************
    public float getMonthEarning(int operation, int month) throws InvalidMonthE, InvalidOperationE {
        if (month < 1 || month > 12) throw new InvalidMonthE();
        if (operation < 1 || operation > 3) throw new InvalidOperationE();
        float earning = 0;
        for (Call call : calls_history) {
            String receiver_country_code = call.getReceiverCountryCode();
            String sender_country_code = call.getSenderCountryCode();
            int call_month = call.getMonth();

            //If the call's month matches the month queried, process it
            if (call_month == month) {
                switch (operation) {
                    case 1:
                        // Get earnings from National Calls (country codes are equal)
                        if (receiver_country_code.equals(sender_country_code)) {
                            // TODO Check with Ramiro
                            earning += getCallCost(call);
                        }
                        break;
                    case 2:
                        // Get earnings from International Calls (country codes are not equal)
                        if (!receiver_country_code.equals(sender_country_code)) {
                            earning += getCallCost(call);
                        }
                        break;
                    case 3:
                        // Get total earnings
                        earning += getCallCost(call);
                        break;
                }
            }
        }

        // Round the earning to 2 decimals
        BigDecimal rounded_earning = new BigDecimal(earning);
        rounded_earning = rounded_earning.setScale(2, RoundingMode.HALF_UP);
        return rounded_earning.floatValue();
    }


    /**
     * Returns an ArrayList of clients which made calls exceeding the specified {@code tax}.
     * <br><br>
     * Each client is followed by a list of countries/provinces the client made those calls to
     */
    public List<String> getOverPays(float max_ax) {
        List<String> overpays = new ArrayList<>();
        for(Call call: calls_history) {
            if (getCallCost(call) > max_ax) {
                String asd = call.getSenderPhone() + "   " + call.getReceiverLocationCode() + "   " + call.getReceiverCountryCode();
                overpays.add(asd);
            }
        }
        return overpays;
    }

    /**
     * Returns an ArrayList of countries called more than {@code k} times
     *
     * @param n      Minimum number of calls a country must have to be returned
     * @param period Period of time in which the calls should have been made.
     *               It has to have the format {@code dd-mm-yyyy}.
     */
    // TODO Error to check the period is valid ************* CHANGE
    public List<Map.Entry<String, Integer>> getHotCountries(String period, int n) throws WrongPeriodFormatE {
        int counter = 0;
        List<String> hot_countries = new ArrayList<>();

        if (checkPeriod(period)) throw new WrongPeriodFormatE();
        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        Map<String, Integer> map = new HashMap<>();

        for(Call call: calls_history){
            if(map.containsKey(call.getReceiverCountryCode())) {
                map.put(call.getReceiverCountryCode(), map.get(call.getReceiverCountryCode()) + 1);
            } else {
                map.put(call.getReceiverCountryCode(), 1);
            }
        }

        List<Map.Entry<String, Integer>> hotCountries = new ArrayList<>();

        for(Map.Entry<String, Integer> entry: map.entrySet()) {
            if(entry.getValue() >= n) {
                hotCountries.add(entry);
            }
        }
        return hotCountries;
    }

    /**
     * Returns the most called province in a certain period
     *
     * @param period Period of time in which the calls should have been made.
     *               It has to have the format {@code dd-mm-yyyy}.
     */
//TODO Check the period CHANGE
    public String getHotProvince(String period) throws WrongPeriodFormatE {
        String hot_province = "";
        int counter = 0;
        int max_counter = -1;

        if (checkPeriod(period)) throw new WrongPeriodFormatE();
        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        for (int i = 0; i < calls_history.size(); i++) {
            for (Call call : calls_history) {
                if (call.getMonth() >= start && call.getMonth() <= end &&
                        call.getReceiverLocationCode().equals(calls_history.get(i).getReceiverLocationCode())) {
                    counter++;
                    if (counter > max_counter) {
                        max_counter = counter;
                        hot_province = calls_history.get(i).getReceiverLocationCode();
                    }
                }
            }
            counter = 0;
        }
        return hot_province;
    }

    /**
     * Returns how much a client has to pay in a certain period
     *
     * @param period Period of time in which the calls should have been made.
     *               It has to have the format {@code dd-mm-yyyy}.
     * @param phone  Phone number of the client
     */
// TODO Error to check the client exists
    public float getClientOwe(String period, String phone) throws WrongPeriodFormatE {
        float owe = 0;

        if (checkPeriod(period)) throw new WrongPeriodFormatE();
        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);


        for (Call call : calls_history) {
            // Check the call is in the period queried and the client who made the call matches
            if (call.getMonth() >= start && call.getMonth() <= end && call.getSenderPhone().equals(phone)) {
                owe += getCallCost(call);
                //System.out.println(i);
            }
        }

        // Round owe to 2 decimals
        BigDecimal rounded_owe = new BigDecimal(owe);
        rounded_owe = rounded_owe.setScale(2, RoundingMode.HALF_UP);
        return rounded_owe.floatValue();
    }
}


