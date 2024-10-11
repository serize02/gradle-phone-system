package org.uclv.models;

import org.uclv.exceptions.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Central implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    // TODO Check whether the name is valid or not
    private String address;
    private List<Client> clients;
    private List<Tax> taxes;
    private List<Call> calls_history;

    public List<Call> getCalls() {
        return calls_history;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public Central(String name, String address, List<Client> clients, List<Call> calls_history, List<Tax> taxes) {
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
     * @return An ordered List of the Central's clients
     */
    public List<Client> getClients() {

        return clients;
    }

    public Client verifyClient(String username, String code) throws InvalidCredentialsE {
        for (Client client : clients) {
            if (client.getUsername().equals(username) && client.getPassword().equals(code)) {
                return client;
            }
        }
        throw new InvalidCredentialsE();
    }

    // TODO Not Used
    public Client getClientByCode(String code) {
        for (Client client : clients) {
            if (client.getPassword().equals(code)) {
                return client;
            }
        }
        return null;
    }

    public void addClient(Client client) throws ClientAlreadyExistsE {
        if (!clients.contains(client)) {
            clients.add(client);
            return;
        }
        throw new ClientAlreadyExistsE();
    }

    public void addCall(Call call) {

        calls_history.add(call);
    }

    public void addTax(Tax tax) {
        taxes.add(tax);
    }

    /**
     *
     * @param period The period to be checked
     * @return {@code 0} if the given period is valid
     *         <br>
     *         {@code 1} if the given period is not valid
     */
    private boolean invalidPeriod(String period) {
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
     * @param call The call we want to know the cost of
     * @return The cost of the given call
     */
    public float getCallCost(Call call) {
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
            if (!receiver_country_code.equals("+053")) {
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
     * @param operation An integer between 1 and 3, each one standing for:
     *                  <ol>
     *                   <li>National Calls</li>
     *                   <li>International Calls</li>
     *                   <li>Total</li>
     *                  </ol>
     * @param month     An integer between 1 and 12 representing each month
     * @return Earnings matching the specified {@code operation} and {@code month}
     */
    public float getMonthEarning(int operation, int month) throws InvalidMonthE {
        if (month < 1 || month > 12) throw new InvalidMonthE();
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
     * @param max_ax The minimum cost a call must have to be returned
     * @return A list of calls that exceed the specified cost
     */
    public List<Call> getOverPays(float max_ax) {
        List<Call> overpays = new ArrayList<>();
        for(Call call: calls_history) {
            if (getCallCost(call) > max_ax) {
                overpays.add(call);
            }
        }
        return overpays;
    }

    /**
     * @param n      Minimum number of calls a country must have to be returned
     * @param period Period of time in which the calls should have been made.
     *               It has to have the format {@code dd-mm-yyyy}.
     * @return A list of countries called more than {@code k} times
     */
    // TODO Error to check the period is valid ************* CHANGE
    public List<Map.Entry<String, Integer>> getHotCountries(String period, int n) throws WrongPeriodFormatE {
        int counter = 0;

        if (invalidPeriod(period)) throw new WrongPeriodFormatE();

        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        Map<String, Integer> map = new HashMap<>();

        for(Call call: calls_history){
            if (call.getMonth() >= start && call.getMonth() <= end){
                if(map.containsKey(call.getReceiverCountryCode())) {
                    map.put(call.getReceiverCountryCode(), map.get(call.getReceiverCountryCode()) + 1);
                } else {
                    map.put(call.getReceiverCountryCode(), 1);
                }
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
     * @param period Period of time in which the calls should have been made.
     *               It has to have the format {@code mm-mm}.
     * @return A list of provinces/locations called in a given period
     */

    public List<Map.Entry<String, Integer>> getProvinces(String period) throws WrongPeriodFormatE {
        if (invalidPeriod(period)) throw new WrongPeriodFormatE();

        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        Map<String, Integer> map = new HashMap<>();

        for(Call call: calls_history){
            if (call.getMonth() >= start && call.getMonth() <= end){
                if(map.containsKey(call.getReceiverLocationCode())) {
                    map.put(call.getReceiverLocationCode(), map.get(call.getReceiverLocationCode()) + 1);
                } else {
                    map.put(call.getReceiverLocationCode(), 1);
                }
            }
        }

        return new ArrayList<>(map.entrySet());
    }

    /**
     * @param phone Phone number of the client
     * @return How much a client has to pay
     */

    public float getClientOwe(String phone) throws ClientDoesNotExistsE {
        float owe = 0;
        boolean client_not_found = true;

        for (Call call : calls_history) {
            // Check the call is in the period queried and the client who made the call matches
            if (call.getSenderPhone().equals(phone)) {
                owe += getCallCost(call);
                client_not_found = false;
            }
        }

        if (client_not_found) {
            throw new ClientDoesNotExistsE();
        }

        // Round owe to 2 decimals
        BigDecimal rounded_owe = new BigDecimal(owe);
        rounded_owe = rounded_owe.setScale(2, RoundingMode.HALF_UP);
        return rounded_owe.floatValue();
    }

//    public void keepClients() throws IOException{
//        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("clients.dat"));
//        for(Client client: clients){
//            os.writeObject(client);
//        }
//        os.close();
//    }
//    public void keepCalls() throws IOException{
//        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("calls.dat"));
//        for(Call call : calls_history){
//            os.writeObject(call);
//        }
//        os.close();
//    }
//
//    public void keepTaxes() throws IOException{
//        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("taxes.dat"));
//        for(Tax tax : taxes){
//            os.writeObject(tax);
//        }
//        os.close();
//    }

    public void exportData(Central central) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("data/data.dat"));
        os.writeObject(central);
        os.close();
    }

    public static Central importData() throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("data/data.dat"));
        Central central = (Central) is.readObject();
        is.close();
        return central;
    }
}


