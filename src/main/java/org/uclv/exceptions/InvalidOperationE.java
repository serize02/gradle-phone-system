package org.uclv.exceptions;

public class InvalidOperationE extends Exception{

    public InvalidOperationE (){
        super("Must be an integer between 1 and 3");
    }
}
