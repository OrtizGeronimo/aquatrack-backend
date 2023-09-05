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
public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer legajo;
    private String tipo;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaFinVigencia;
}
