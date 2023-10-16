package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListarRepartosDTO {

    private Long id;
    private Long idRuta;
    private String repartidor;
    private String estado;
    private Integer cantEntregas;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaEjecucion;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraInicio;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraFin;
    private double latitudInicio;
    private double longitudInicio;
    private String nombreRuta;
    private String observaciones;
}
