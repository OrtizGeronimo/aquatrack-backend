package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RepartoDTO {

    private Long id;
    private List<EntregaDTO> entregas;
    private EstadoRepartoDTO estadoReparto;
    private RutaDTO ruta;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaEjecucion;
}
