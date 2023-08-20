package com.example.aquatrack_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.aquatrack_backend.dto.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ InternalAuthenticationServiceException.class })
  public ResponseEntity<?> handleUnauthorizedCredentials(Exception ex) {
    ex.printStackTrace();
    if (ex instanceof BadCredentialsException) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ErrorResponseDTO.builder()
              .message("Credenciales de usuario inválidas").build());
    }

    if (ex instanceof DisabledException) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ErrorResponseDTO.builder()
              .message("El usuario con el que intenta iniciar sesión con Aquatrack fue dado de baja.").build());
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponseDTO.builder()
            .message("Error de autenticación inesperado, intente mas tarde." + ex.getClass().getCanonicalName())
            .build());
  }
}
