package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.exception.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.aquatrack_backend.dto.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ AuthenticationException.class })
  public ResponseEntity<?> handleUnauthorizedCredentials(Exception ex) {
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
            .message("Error de autenticación inesperado, intente mas tarde.")
            .build());
  }

  @ExceptionHandler({ ClienteWebUnauthorizedException.class })
  public ResponseEntity<?> handleClienteWebException(Exception ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({ FailedToAuthenticateUserException.class })
  public ResponseEntity<?> handleFailedToAuthenticate(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({ EntidadNoVigenteException.class })
  public ResponseEntity<?> handleEntidadNoVigente(Exception ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({ RecordNotFoundException.class })
  public ResponseEntity<?> handleRecordNotFound(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({ EmpresaSinCoberturaException.class })
  public ResponseEntity<?> handleSinCobertura(Exception ex) {
    HashMap<String, Object> response = new HashMap<>();
    response.put("id", null);
    response.put("nombreEmpresa", null);
    response.put("vertices", new ArrayList<>());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @ExceptionHandler({ UserWithOneRolePresentException.class })
  public ResponseEntity<?> handleDomainException(Exception ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({ AccessDeniedException.class })
  public ResponseEntity<?> handleDeniedAccessException(Exception ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponseDTO.builder()
            .message(
                "No cuenta con los permisos necesarios para realizar esta acción, contáctese con un administrador.")
            .build());
  }

  @ExceptionHandler({ ClienteNoValidoException.class })
  public ResponseEntity<?> handleClientNotValidatedException(ClienteNoValidoException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getErrors());
  }

  @ExceptionHandler({ UserNoValidoException.class })
  public ResponseEntity<?> handleUserNotValidatedException(UserNoValidoException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getErrors());
  }

  @ExceptionHandler({ClienteNoCubiertoApp.class})
  public ResponseEntity<?> handleClienteNoCubierto(ClienteNoCubiertoApp ex){
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler({EntidadNoValidaException.class})
  public ResponseEntity<?> handleClienteWebUpdate(EntidadNoValidaException ex){
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getErrors());
  }

  @ExceptionHandler({UsuarioYaValidadoException.class})
  public ResponseEntity<?> handleUsuarioYaValidado(UsuarioYaValidadoException ex){
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler({MailException.class})
  public ResponseEntity<?> handleMailException(MailException ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponseDTO.builder()
            .message("Ocurrió un error al mandar el correo electŕonico. Intente nuevamente mas tarde.")
            .build());
  }

  @ExceptionHandler({ValidacionException.class})
  public ResponseEntity<?> handleValidacionException(ValidacionException ex){
    if(ex.getErrors() != null){
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getErrors());
    }else{
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseDTO.builder()
            .message(ex.getMessage())
            .build());
    }
  }

  @ExceptionHandler({ Exception.class })
  public ResponseEntity<?> handleUnexpectedException(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponseDTO.builder()
            .message("Error inesperado del servidor, intente mas tarde.")
            .build());
  }
}
