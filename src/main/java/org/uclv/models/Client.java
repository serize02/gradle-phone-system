package org.uclv.models;

import org.uclv.exceptions.PhoneAlreadyExists;
import org.uclv.exceptions.PhoneNumberDoesNotExistsE;
import org.uclv.exceptions.WrongCodeFormatE;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String username;
    private String password;
    private List<PhoneNumber> phone_numbers = new ArrayList<>();
    private char type;

    public Client(String username, String password, char type) throws WrongCodeFormatE {
        verifyFormat(username);
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    // TODO check the letters and numbers
    public String getPassword() {
        return password;
    }

    public char getType() {
        return type;
    }

    //TODO check the numbers
    public List<PhoneNumber> getPhoneNumbers() {
        return phone_numbers;
    }

    private void verifyFormat(String username) throws WrongCodeFormatE {
        boolean wrong_username = false;
        int index = 0;

        while (index < username.length() && !wrong_username) {
            if (!Character.isLetterOrDigit(username.charAt(index))) {
                wrong_username = true;
            }
            index++;
        }

        if (wrong_username) {
            throw new WrongCodeFormatE();
        }

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
