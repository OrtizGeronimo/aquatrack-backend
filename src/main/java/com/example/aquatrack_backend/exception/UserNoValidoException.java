package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class UserNoValidoException extends Exception{

    private HashMap<String, String> errors;
    public UserNoValidoException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
