package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.model.dto.LoginResponseDTO;
import com.example.aquatrack_backend.service.UsuarioServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(maxAge = 3600)
public class UsuarioControlador {

    @Autowired
    private UsuarioServicioImpl servicio;

    @PostMapping(value = "/login",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody Usuario usuario){
        return ResponseEntity.ok().body(servicio.login(usuario.getDireccionEmail(), usuario.getContraseña()));
    }

    @GetMapping(value = "/current")
    public ResponseEntity<?> getCurrentUser(){
        return ResponseEntity.ok().body(servicio.getCurrentUser());
    }

}
