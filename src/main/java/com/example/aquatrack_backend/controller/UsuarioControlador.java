package com.example.aquatrack_backend.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.aquatrack_backend.dto.LoginRequestDTO;
import com.example.aquatrack_backend.service.UsuarioServicioImpl;

@RestController
@RequestMapping("/users")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicioImpl servicio;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO usuario){
        return ResponseEntity.ok().body(servicio.login(usuario.getDireccionEmail(), usuario.getContraseña()));
    }

    @GetMapping(value = "/current")
    public ResponseEntity<?> getCurrentUser(){
        return ResponseEntity.ok().body(servicio.getCurrentUser());
    }
}