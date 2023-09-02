package com.example.aquatrack_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CoberturaDTO {

    private Long id;
    private String nombreEmpresa;
    private List<UbicacionDTO> ubicacions;
}
