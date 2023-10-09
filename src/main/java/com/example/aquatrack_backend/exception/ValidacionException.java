package com.example.aquatrack_backend.exception;

import java.util.HashMap;

public class ValidacionException extends Exception{

    private HashMap<String, String> errors;
    public ValidacionException(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public ValidacionException(String message) {
        super(message);
    }
}
