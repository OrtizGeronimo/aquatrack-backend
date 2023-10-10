package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.RegisterRequestDTO;
import com.example.aquatrack_backend.dto.UpdateUserDTO;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.ChangePasswordDTO;
import com.example.aquatrack_backend.dto.LoginRequestDTO;
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.exception.PasswordDistintasException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.UsuarioServicio;

@RestController
@RequestMapping("/users")
public class UsuarioControlador {

  @Autowired
  private UsuarioServicio usuarioServicio;
  private ValidationHelper validationHelper = new ValidationHelper<>();

  @PostMapping(value = "/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDTO usuario) {
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

  @GetMapping(value = "")
  public ResponseEntity<?> getUserProfile(){
    return ResponseEntity.ok().body(usuarioServicio.getUserProfile());
  }

  @PutMapping(value = "")
  public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserDTO usuario){
    return ResponseEntity.ok().body(usuarioServicio.updateUserProfile( usuario));
  }

  @GetMapping(value = "/current")
  public ResponseEntity<?> getCurrentUser() throws FailedToAuthenticateUserException {
    return ResponseEntity.ok().body(usuarioServicio.getCurrentUser());
  }

  @PutMapping(value = "/changePassword")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO dto) throws PasswordDistintasException {
    return ResponseEntity.ok().body(usuarioServicio.changePassword(dto));
  }
}