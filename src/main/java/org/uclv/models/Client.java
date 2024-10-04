package org.uclv.models;



import org.uclv.exceptions.PhoneAlreadyExists;
import org.uclv.exceptions.PhoneNumberDoesNotExistsE;

import java.util.ArrayList;
import java.util.List;

public class Client {

    // TODO toString
    private String username;
    private String code;
    private List<PhoneNumber> phone_numbers = new ArrayList<>();
    private char type;

    public Client(String username, String code, char type) {
        this.username = username;
        this.code = code;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    // TODO check the letters and numbers
    public String getCode() {
        return code;
    }

    public char getType() {
        return type;
    }

    //TODO check the numbers
    public List<PhoneNumber> getPhoneNumbers() {
        return phone_numbers;
    }

    //CHANGE
    public void addPhoneNumber(PhoneNumber phone) throws PhoneAlreadyExists {
        for(PhoneNumber number : phone_numbers){
            if(number.equals(phone)){
                throw new PhoneAlreadyExists();
            }
        }
        phone_numbers.add(phone);
    }

    public void removePhoneNumber(PhoneNumber phone) throws PhoneNumberDoesNotExistsE {
        if (phone_numbers.contains(phone)) {
            phone_numbers.remove(phone);
        } else throw new PhoneNumberDoesNotExistsE();
    }

}
