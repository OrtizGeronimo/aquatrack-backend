package com.example.aquatrack_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardarEmpleadoDTO {

  @NotBlank()
  private String nombre;

  @NotBlank()
  private String apellido;

  private Integer legajo;

  private String numTelefono;

  private LocalDate fechaIngreso;

  private Long tipo;

  private UsuarioAddEmpleadoDTO usuario;
  
}