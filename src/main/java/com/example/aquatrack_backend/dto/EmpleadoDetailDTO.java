package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadoDetailDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private Integer legajo;
    private String numTelefono;
    private Long tipo;
    private LocalDate fechaIngreso;
    private LocalDate fechaFinVigencia;
    private UsuarioAddEmpleadoDTO usuario;
}
