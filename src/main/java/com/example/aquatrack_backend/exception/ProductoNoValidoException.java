package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class ProductoNoValidoException extends Exception{

    private HashMap<String, String> errors;
    public ProductoNoValidoException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
