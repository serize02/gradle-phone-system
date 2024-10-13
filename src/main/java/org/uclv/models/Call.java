package org.uclv.models;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Call implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sender_country_code;
    private String sender_location_code;
    private String sender_phone;
    private String receiver_country_code;
    private String receiver_location_code;
    private String receiver_phone;
    private int month;
    private int time; // in sec

    public Call(String sender_country_code, String sender_location_code, String sender_phone, String receiver_country_code, String receiver_location_code, String receiver_phone, int month, int time) {
        this.sender_country_code = sender_country_code;
        this.sender_location_code = sender_location_code;
        this.sender_phone = sender_phone;
        this.receiver_country_code = receiver_country_code;
        this.receiver_location_code = receiver_location_code;
        this.receiver_phone = receiver_phone;
        this.month = month;
        this.time = time;
    }

    public Call (String Dayos, int time){
        String [] s = Dayos.split(String.valueOf('*'));
        receiver_country_code=s[0];
        receiver_location_code=s[1];
        receiver_phone=s[2];
        sender_country_code=s[3];
        sender_location_code=s[4];
        sender_phone=s[5];
        month=Integer.parseInt(s[6]);
        this.time=time;
    }

    public String getSenderCountryCode() {
        return sender_country_code;
    }

    public String getSenderLocationCode() {
        return sender_location_code;
    }

    public String getSenderPhone() {
        return sender_phone;
    }

    public String getReceiverLocationCode() {
        return receiver_location_code;
    }

    public String getReceiverCountryCode() {
        return receiver_country_code;
    }

    public String getReceiverPhone() {
        return receiver_phone;
    }

    public int getMonth() {
        return month;
    }

    public int getTime() {
        return time;
    }

    /**
     * @param tax La llamada de la cual queremos conocer el costo
     * @return El costo de la llamada
     */
    public float getCost(float tax) {
        float cost = 0;

        // Si el codigo de pais del emisor y el destinatario son iguales, la llamada es nacional
        if (receiver_country_code.equals(sender_country_code)) {
            cost = Math.abs(Integer.parseInt(receiver_location_code) - Integer.parseInt(sender_location_code));

            // Si los codigos de localidad son iguales, es una llamada interprovincial
            if (cost == 0) {
                cost = (float) time / 60;
            } else {
                cost *= (float) time / 60;
            }
        } else {
            String country_code;
            String location_code;

            // Como la central esta en Cuba, uno de los codigos de pais debe ser +053. Obtener el que no lo sea
            if (!receiver_country_code.equals("+053")) {
                country_code = receiver_country_code;
            } else {
                country_code = sender_country_code;
            }

            // Convertir el tiempo a minutos
            cost = (float) time / 60;
            cost *= tax;
        }

        // Redondear el costo a 2 decimales
        BigDecimal rounded_cost = new BigDecimal(cost);
        rounded_cost = rounded_cost.setScale(2, RoundingMode.HALF_UP);
        return rounded_cost.floatValue();
    }

    @Override
    public String toString() {
        return receiver_country_code + "*" + receiver_location_code + "*" + receiver_phone + "*" + sender_country_code + "*" + sender_location_code + "*" + sender_phone + "*" + month;
    }

}

