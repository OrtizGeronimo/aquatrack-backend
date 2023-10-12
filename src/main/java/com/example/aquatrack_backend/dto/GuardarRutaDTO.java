package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardarRutaDTO {
    private Long id;
    private String nombre;
    private List<Long> idDiasSemana;
    private List<DomiciliosRutaDTO> domiciliosRuta;
    private LocalDateTime fechaFinVigencia;
}