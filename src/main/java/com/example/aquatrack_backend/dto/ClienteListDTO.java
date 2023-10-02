package com.example.aquatrack_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
  private LocalDate fechaCreacion;
  private LocalDate fechaFinVigencia;
}