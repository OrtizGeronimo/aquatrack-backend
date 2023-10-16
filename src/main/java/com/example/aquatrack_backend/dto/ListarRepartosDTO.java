package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ListarRepartosDTO {

    private Long id;
    private Long idRuta;
    private String repartidor;
    private String estado;
    private Integer cantEntregas;
    private LocalDate fechaEjecucion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private double latitudInicio;
    private double longitudInicio;
    private String nombreRuta;
    private String observaciones;
}
