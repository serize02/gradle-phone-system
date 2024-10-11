package org.uclv.models;


import org.uclv.exceptions.WrongPhoneNumberFormatE;

import java.io.Serializable;

public class PhoneNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    private String country_code;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String number;

    public String getCountryCode() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public PhoneNumber(String country_code, String number) throws WrongPhoneNumberFormatE {
        verifyFormat(country_code, number);
        this.country_code = country_code;
        this.number = number;
    }

    private void verifyFormat(String cc, String number) throws WrongPhoneNumberFormatE {
        if(cc.isEmpty() || number.isEmpty() || cc.charAt(0) != '+' || cc.length() != 4){
            throw new WrongPhoneNumberFormatE();
        }

        int index = 1;
        boolean wrong_format = false;

        while(index < cc.length() && !wrong_format) {
            if(!Character.isDigit(cc.charAt(index))){
                wrong_format = true;
            }
            index++;
        }

        index = 0;

        while(index < number.length() && !wrong_format) {
            if(!Character.isDigit(number.charAt(index))){
                wrong_format = true;
            }
            index++;
        }

        if (wrong_format)
            throw new WrongPhoneNumberFormatE();
    }

    @Override
    public String toString() {
        return country_code + " " + number;
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }
}
