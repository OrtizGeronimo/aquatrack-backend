package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.RegisterRequestDTO;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.LoginRequestDTO;
import com.example.aquatrack_backend.exception.ClienteWebUnauthorizedException;
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.service.UsuarioServicio;

@RestController
@RequestMapping("/users")
public class UsuarioControlador {

  @Autowired
  private UsuarioServicio usuarioServicio;
  private ValidationHelper validationHelper = new ValidationHelper<>();

  @PostMapping(value = "/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO usuario) throws ClienteWebUnauthorizedException {
    if(validationHelper.hasValidationErrors(usuario)){
      return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(usuario));
    }
    return ResponseEntity.ok().body(usuarioServicio.login(usuario.getDireccionEmail(), usuario.getContraseña()));
  }

  @PostMapping(value="/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO usuario){
    if(validationHelper.hasValidationErrors(usuario)){
      return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(usuario));
    }
    return ResponseEntity.ok().body(usuarioServicio.clientRegister(usuario));
  }

  @GetMapping(value = "/current")
  public ResponseEntity<?> getCurrentUser() throws FailedToAuthenticateUserException {
    return ResponseEntity.ok().body(usuarioServicio.getCurrentUser());
  }
}