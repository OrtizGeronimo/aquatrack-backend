package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.ClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteControlador {

    @Autowired
    private ClienteServicio service;

    @GetMapping("/login")
    public ResponseEntity<?> login(String usuario, String contraseña){
        return ResponseEntity.ok().build();
    }
}
