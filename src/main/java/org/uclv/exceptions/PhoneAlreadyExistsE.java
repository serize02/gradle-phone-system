package org.uclv.exceptions;

public class PhoneAlreadyExists extends Exception{

    public PhoneAlreadyExists(){
        super("This phone number already exists");
    }
}
