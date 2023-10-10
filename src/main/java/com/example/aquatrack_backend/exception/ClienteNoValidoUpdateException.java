package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class ClienteNoValidoUpdateException extends Exception{

    private HashMap<String, String> errors;
    public ClienteNoValidoUpdateException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
