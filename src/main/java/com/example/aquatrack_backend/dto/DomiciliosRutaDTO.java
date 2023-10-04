package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  DomiciliosRutaDTO {
    private Long idDomicilio;
    private List<Long> idDiasSemana;
}