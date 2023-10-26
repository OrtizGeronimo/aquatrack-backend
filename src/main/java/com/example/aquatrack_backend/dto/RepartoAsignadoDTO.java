package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RepartoAsignadoDTO {
    private Long id;
    private String ruta;
    private Long cantEntregas;
    private LocalDateTime fechaEjecucion;
    private LocalDateTime fechaHoraInicio;
}
