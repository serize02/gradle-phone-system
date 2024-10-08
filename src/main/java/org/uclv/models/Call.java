package org.uclv.models;

public class Call {
    private String sender_country_code;
    private String sender_location_code;
    private String sender_phone;
    private String receiver_country_code;
    private String receiver_location_code;
    private String receiver_phone;
    private int month;
    private int time; // in sec

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

    @Override
    public String toString() {
        return receiver_country_code + "*" + receiver_location_code + "*" + receiver_phone + "*" + sender_country_code + "*" + sender_location_code + "*" + sender_phone + "*" + month;
    }
}

