package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinVigencia;
    private String numTelefono;
    private Long usuario_id;
}
