package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditCoberturaUbicacionDTO {
    private Boolean esEmpresa;
    private Double latitud;
    private Double longitud;
}
