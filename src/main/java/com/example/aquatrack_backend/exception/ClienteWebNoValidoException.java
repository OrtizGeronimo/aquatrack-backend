package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class ClienteWebNoValidoException extends Exception{

    private HashMap<String, String> errors;
    public ClienteWebNoValidoException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
