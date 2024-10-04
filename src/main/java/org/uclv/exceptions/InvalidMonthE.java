package org.uclv.exceptions;

public class InvalidMonthE extends Exception{
    public InvalidMonthE(){
        super("Must be an integer between 1 and 12");
    }
}
