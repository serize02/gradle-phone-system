package org.uclv.exceptions;

public class WrongTaxCodeE extends Exception{
    public WrongTaxCodeE(){
        super("Tax already exists");
    }
}
