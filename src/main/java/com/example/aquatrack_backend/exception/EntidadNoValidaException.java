package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class EntidadNoValidaException extends Exception{

    private HashMap<String, String> errors;
    public EntidadNoValidaException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
