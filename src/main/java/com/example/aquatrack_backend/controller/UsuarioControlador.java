package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.*;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.service.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<?> register(@RequestBody RegisterRequestDTO usuario) throws UserNoValidoException, RecordNotFoundException {
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
  public ResponseEntity<?> changePasswordProfile(@RequestBody ChangePasswordDTO dto) throws PasswordDistintasException {
    return ResponseEntity.ok().body(usuarioServicio.changePasswordProfile(dto));
  }

  @PutMapping(value = "/changePassword")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordLoginDTO dto) {
   return ResponseEntity.ok().body(usuarioServicio.updatePassword(dto));
 }

}