package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RutaListDTO {

    private Long id;
    private String nombre;
    private LocalDateTime fechaCreacion;
    private List<Long> idDiasSemana;
    private Integer domiciliosAVisitar;
    private LocalDateTime fechaFinVigencia;
}
