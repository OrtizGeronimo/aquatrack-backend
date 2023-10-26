package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EntregaMobileDTO {
    private Long id;
    private String nombreCliente;
    private String domicilio;
    private BigDecimal montoRecaudar;
    private String estado;
    private String observaciones;
}
