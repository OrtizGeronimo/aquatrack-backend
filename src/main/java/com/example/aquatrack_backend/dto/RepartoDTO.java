package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RepartoDTO {

    private Long id;
    private List<EntregaDTO> entregas;
    private EstadoRepartoDTO estadoReparto;
    private RutaDTO ruta;
    private LocalDate fechaEjecucion;

}
