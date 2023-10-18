package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {
  private long id;
  private String nombre;
  private LocalDateTime fechaCreacion;
  private LocalDateTime fechaFinVigencia;
}
