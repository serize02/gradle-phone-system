package org.uclv.models;


import org.uclv.exceptions.WrongPhoneNumberFormatE;

public class PhoneNumber {
    private String country_code;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String number;

    public String getCountry_code() {
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

        if(cc.length() == 0 || number.length() == 0 || cc.charAt(0) != '+'){
            throw new WrongPhoneNumberFormatE();
        }
        for(int i = 1; i < cc.length(); i++){
            if(!Character.isDigit(cc.charAt(i))){
                throw new WrongPhoneNumberFormatE();
            }
        }
        for(int i = 0; i < number.length(); i++){
            if(!Character.isDigit(number.charAt(i))){
                throw new WrongPhoneNumberFormatE();
            }
        }
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
