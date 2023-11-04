package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregaWebDTO {
    private Long id;
    private String nombreCliente;
    private String domicilio;
    private BigDecimal montoRecaudar;
    private String estado;
    private String observaciones;
    private Integer ordenVisita;
    private LocalDateTime fechaEjecucion;
    private LocalDateTime fechaHoraVisita;
    private BigDecimal montoEntregado;
    private BigDecimal montoRecaudado;
    private String medioPago;
    private String observacionesEntrega;
    private Long repartoId;
    private String nombreRepartidor;
}
