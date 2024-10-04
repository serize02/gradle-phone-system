package org.uclv.exceptions;

public class WrongPhoneNumberFormatE extends Exception {
    public WrongPhoneNumberFormatE() {
        super("The phone number is not in the correct format.");
    }
}
