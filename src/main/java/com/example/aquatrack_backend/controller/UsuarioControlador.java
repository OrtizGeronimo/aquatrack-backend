package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.model.dto.LoginResponseDTO;
import com.example.aquatrack_backend.service.UsuarioServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicioImpl servicio;

    @PostMapping("/login")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> login(Usuario usuario){
        return ResponseEntity.ok().body(servicio.login(usuario.getDireccionEmail(), usuario.getContraseña()));
    }


}
