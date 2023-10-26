package com.example.aquatrack_backend.exception;

import lombok.Data;

import java.util.HashMap;

@Data
public class PedidoNoValidoException extends Exception{

    private HashMap<String, String> errors;
    public PedidoNoValidoException(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
