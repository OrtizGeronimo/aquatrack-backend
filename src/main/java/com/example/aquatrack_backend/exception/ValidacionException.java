package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class ValidacionException extends Exception{

    private HashMap<String, String> errors;
    public ValidacionException(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public ValidacionException(String message) {
        super(message);
    }
}
