package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer dni;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaFinVigencia;
    private String numTelefono;
    private String calle;
    private Integer numero;
    private String pisoDepto;
    private String observaciones;
    private Long empresaId;
    private Long usuarioId;
}
