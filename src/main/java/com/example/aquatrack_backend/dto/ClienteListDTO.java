package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteListDTO {
  private Long id;
  private String dni;
  private String nombreCompleto;
  private String domicilio;
  private String numTelefono;
  private String direccionEmail;
  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaCreacion;
  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaFinVigencia;
}