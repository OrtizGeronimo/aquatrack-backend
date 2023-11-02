package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoListMobileDTO {
    private Long id;
    private String tipoPedido;
    private String estadoPedido;
    private LocalDateTime fechaHoraEntrega;
    private LocalDate fechaCoordinadaEntrega;
    private LocalDateTime fechaFinVigencia;
    private BigDecimal precio;
    private List<PedidoProductoDTO> productos;
}
