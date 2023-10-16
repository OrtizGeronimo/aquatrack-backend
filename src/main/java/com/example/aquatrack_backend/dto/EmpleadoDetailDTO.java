package com.example.aquatrack_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaFinVigencia;
    private UsuarioAddEmpleadoDTO usuario;
}
