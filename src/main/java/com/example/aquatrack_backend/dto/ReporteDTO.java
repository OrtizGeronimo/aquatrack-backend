package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteDTO {
    private BigDecimal montoRecaudado;
    private Long cantidadEntregas;
    private Long cantidadClientes;
    private List<String> labelProductosEntregados;
    private List<Long> valueProductosEntregados;
    private List<String> labelEntregas;
    private List<Long> valueEntregas;
}
