package org.uclv.models;

import org.uclv.exceptions.PhoneAlreadyExistsE;
import org.uclv.exceptions.PhoneNumberDoesNotExistsE;
import org.uclv.exceptions.WrongCodeFormatE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private List<PhoneNumber> phone_numbers = new ArrayList<>();
    private char type;

    public Client(String username, String password, char type) throws WrongCodeFormatE {
        verifyFormat(username,password);
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

    /**
     * Verifica si el nombre de usuario tiene el formato establecido y la contraseña no está vacía
     * @param username Nombre de usuario del cliente
     * @param password Contraseña del cliente
     */
    private void verifyFormat(String username,String password) throws WrongCodeFormatE {
        boolean wrong_username = (username.length() !=6 || password.isEmpty());
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

    /**
     * Agrega el número de teléfono pasado por parámetro al listado de números de teléfono del cliente
     * @param phone Número de teléfono que se desea agregar
     * @throws PhoneAlreadyExistsE Verifica que el número no exista ya, antes de agregarlo
     */
    public void addPhoneNumber(PhoneNumber phone) throws PhoneAlreadyExistsE {
        for(PhoneNumber number : phone_numbers){
            if(number.equals(phone)){
                throw new PhoneAlreadyExistsE();
            }
        }
        phone_numbers.add(phone);
    }

    /**
     * Elimina el número de teléfono pasado por parámetro del listado de números de teléfono del cliente
     * @param phone Número de teléfono que se desea eliminar
     * @throws PhoneNumberDoesNotExistsE Verifica que el número exista, antes de eliminarlo
     */
    public void removePhoneNumber(PhoneNumber phone) throws PhoneNumberDoesNotExistsE {
        if (phone_numbers.contains(phone)) {
            phone_numbers.remove(phone);
        } else throw new PhoneNumberDoesNotExistsE();
    }

}
