package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoListDTO {

    private Long id;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDate fechaCoordinadaEntrega;
    private Boolean entregable;
    private DomicilioDTO domicilio;
    private String estadoPedido;
    private String tipoPedido;
    private List<PedidoProductoDTO> pedidoProductos;
    private BigDecimal totalPedido;
    private LocalDateTime fechaFinVigencia;
}
