package org.uclv.models;

import org.uclv.exceptions.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Central implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
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

    /**
     *
     * @param country_code El codigo de pais del cual se quiere conocer la tarifa
     * @return La tarifa aplicada a {@code country_code}
     */
        public float getTaxValue(String country_code) {
        int i = 0;
        float tax = 1;

        // Buscar la tarifa que se le aplica al pais extranjero (siempre existe)
        while (i < taxes.size() &&  !taxes.get(i).getCountryCode().equals(country_code)) {
            i++;
        }

        if (i != taxes.size()) {
            tax = taxes.get(i).getValue();
        }

        return tax;
    }

    public Central(String name, String address, List<Client> clients, List<Call> calls_history, List<Tax> taxes) {
        this.name = name;
        this.address = address;
        this.clients = clients;
        this.calls_history = calls_history;
        this.taxes = taxes;
    }

    public Central(String name, String address) {
        this.name = name;
        this.address = address;
        this.clients = new ArrayList<>();
        this.calls_history = new ArrayList<>();
        this.taxes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    /**
     * @return Una lista ordenada de clientes
     */
    public List<Client> getClients() {
        return clients;
    }

    /**
     *
     * @param phone El numero telefonico del cliente
     * @return El nombre de usuario del cliente3
     */
    public String getClientByPhoneNumber(PhoneNumber phone) {
        for (Client client : clients) {
            for (PhoneNumber current_phone : client.getPhoneNumbers()) {
                if (current_phone.equals(phone)) {
                    return client.getUsername();
                }
            }
        }

        return "";
    }
    /**
     * Verifica si el cliente se encuentra registrado en la central
     * @param username Nombre de usuario del cliente
     * @param password Contraseña del cliente
     * @return Cliente verificado
     */
    public Client verifyClient(String username, String password) throws InvalidCredentialsE {
        for (Client client : clients) {
            if (client.getUsername().equals(username) && client.getPassword().equals(password)) {
                return client;
            }
        }
        throw new InvalidCredentialsE();
    }

    /**
     * Agrega ordenadamente un cliente a la lista de clientes de la central
     * @param client Cliente a ser agregado
     */
    public void addClient(Client client) throws ClientAlreadyExistsE {
        if (!clients.contains(client)) {
            clients.add(client);
            clients.sort(new Comparator<Client>() {
                @Override
                public int compare(Client o1, Client o2) {
                    return o1.getUsername().compareTo(o2.getUsername());
                }
            });
        } else {
            throw new ClientAlreadyExistsE();
        }
    }

    /**
     * Agrega una llamada a la lista de llamadas de la central
     * @param call Llamada a ser agregada
     */
    public void addCall(Call call) {
        calls_history.add(call);
    }

    /**
     * Agrega una tarifa a la lista de tarifas de la central
     * @param tax Tarifa a ser agregada
     */
    public void addTax(Tax tax) {
        taxes.add(tax);
    }

    /**
     * Verifica si un periodo dado es valido
     * @param period El periodo a ser verificado
     * @return {@code 0} si el periodo es valido
     *         <br>
     *         {@code 1} si el periodo es invalido
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
     * @param operation Un entero entre 1 y 3, cada uno correspondiendo a:
     *                  <ol>
     *                   <li>Llamadas nacionales</li>
     *                   <li>Llamadas interncionales</li>
     *                   <li>Total</li>
     *                  </ol>
     * @param month     Un entero entre 1 y 12 representando el mes
     * @return Las ganancias que responden a {@code operation} y {@code month}
     */
    public float getMonthEarning(int operation, int month) throws InvalidMonthE {
        if (month < 1 || month > 12) throw new InvalidMonthE();
        float earning = 0;
        for (Call call : calls_history) {
            String receiver_country_code = call.getReceiverCountryCode();
            String sender_country_code = call.getSenderCountryCode();
            int call_month = call.getMonth();

            //Procesar la llamada si coincide con el mes especificado
            if (call_month == month) {
                switch (operation) {
                    case 1:
                        // Obtener las ganancias nacionales (los codigos son iguales)
                        if (receiver_country_code.equals(sender_country_code)) {
                            earning += call.getCost(getTaxValue(receiver_country_code));
                        }
                        break;
                    case 2:
                        // Obtener ganancias de llamadas internacionales
                        if (!receiver_country_code.equals(sender_country_code)) {
                            earning += call.getCost(getTaxValue(receiver_country_code));
                        }
                        break;
                    case 3:
                        // Obtener las ganancias totales
                        earning += call.getCost(getTaxValue(receiver_country_code));
                        break;
                }
            }
        }

        // Redondear a 2 decimales
        BigDecimal rounded_earning = new BigDecimal(earning);
        rounded_earning = rounded_earning.setScale(2, RoundingMode.HALF_UP);
        return rounded_earning.floatValue();
    }


    /**
     * @param max_ax El costo minimo que debe tener una llamada para ser devuelta
     * @return Una lista de llamadas que exceden {@code max_ax}
     */
    public List<Call> getOverPays(float max_ax) {
        List<Call> overpays = new ArrayList<>();
        for(Call call: calls_history) {
            if (call.getCost(getTaxValue(call.getReceiverCountryCode())) > max_ax) {
                overpays.add(call);
            }
        }
        return overpays;
    }

    /**
     * @param n      Minimo numero de llamadas que debe tener un pais para ser devuelto.
     * @param period Periodo de tiempo en el cual deben ser hechas las llamadas.
     *               Debe tener el formato {@code mm-mm}.
     * @return Una lista de paises a los que se realizaron más de {@code n} llamadas.
     */
    public List<Map.Entry<String, Integer>> getHotCountries(String period, int n) throws WrongPeriodFormatE {
        // Si el periodo no es valido, se eleva la excepcion
        if (invalidPeriod(period)) throw new WrongPeriodFormatE();

        // Obtener el mes de inicio y de final
        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        Map<String, Integer> map = new HashMap<>();

        // Agregar al mapa las llamadas por pais
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

        // Agregar los paises a los que se hicieron mas de n llamadas
        for(Map.Entry<String, Integer> entry: map.entrySet()) {
            if(entry.getValue() >= n) {
                hotCountries.add(entry);
            }
        }
        return hotCountries;
    }

    /**
     * @param period Periodo en que las llamadas deben haber sido realizadas.
     *               Debe tener el formato {@code mm-mm}.
     * @return Una lista de paises a los cuales se llamo en el periodo establecido.
     */

    public List<Map.Entry<String, Integer>> getProvinces(String period) throws WrongPeriodFormatE {
        // Si el periodo no es valido, se eleva la excepcion
        if (invalidPeriod(period)) throw new WrongPeriodFormatE();

        // Obtener el mes de inicio y de final
        String[] s = period.split("-");
        int start = Integer.parseInt(s[0]);
        int end = Integer.parseInt(s[1]);

        Map<String, Integer> map = new HashMap<>();

        // Agregar al mapa las llamadas por provincia
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
     * @param phone Numero telefonico del cliente
     * @return Cuanto el cliente debe pagar por llamadas realizadas a traves de {@code phone}.
     */

    public float getClientOwe(String phone) throws ClientDoesNotExistsE {
        float owe = 0;
        boolean client_not_found = true;

        for (Call call : calls_history) {
            // Verificar emisor de la llamada
            if (call.getSenderPhone().equals(phone)) {
                owe += call.getCost(getTaxValue(call.getReceiverCountryCode()));
                client_not_found = false;
            }
        }

        // Si el cliente no se encontro, se eleva un error
        if (client_not_found) {
            throw new ClientDoesNotExistsE();
        }

        // Redondear el monto a pagar a 2 decimales
        BigDecimal rounded_owe = new BigDecimal(owe);
        rounded_owe = rounded_owe.setScale(2, RoundingMode.HALF_UP);
        return rounded_owe.floatValue();
    }
}


