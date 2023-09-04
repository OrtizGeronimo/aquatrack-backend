package com.example.aquatrack_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardarProductoDTO {

  @NotBlank()
  private String nombre;

  @NotBlank()
  private String descripcion;
  
}