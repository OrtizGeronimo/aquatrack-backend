package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer dni;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaFinVigencia;
    private String numTelefono;
    private String calle;
    private Integer numero;
    private String pisoDepto;
    private String observaciones;
    private Long empresaId;
    private Long usuarioId;
}
