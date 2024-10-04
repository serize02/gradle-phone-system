package org.uclv.exceptions;

public class ClientAlreadyExistsE extends Exception {
    public ClientAlreadyExistsE() {
        super("Client already exists");
    }
}
