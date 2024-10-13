package org.uclv.models;











import org.uclv.exceptions.PhoneAlreadyExistsE;
import org.uclv.exceptions.PhoneNumberDoesNotExistsE;
import org.uclv.exceptions.WrongCodeFormatE;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    public String getPassword() {
        return password;
    }

    public char getType() {
        return type;
    }

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
    public void addPhoneNumber(PhoneNumber phone) throws PhoneAlreadyExistsE {
        for(PhoneNumber number : phone_numbers){
            if(number.equals(phone)){
                throw new PhoneAlreadyExistsE();
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
