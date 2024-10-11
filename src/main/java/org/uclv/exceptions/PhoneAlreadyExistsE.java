package org.uclv.exceptions;

public class PhoneAlreadyExistsE extends Exception{

    public PhoneAlreadyExistsE(){
        super("This phone number already exists");
    }
}
