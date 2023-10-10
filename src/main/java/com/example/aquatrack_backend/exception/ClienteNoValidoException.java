package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class ClienteNoValidoException extends Exception{

    private HashMap<String, String> errors;
    public ClienteNoValidoException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
