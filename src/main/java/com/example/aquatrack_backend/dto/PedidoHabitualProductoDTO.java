package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PedidoHabitualProductoDTO {
    private Long idProducto;
    private Integer cantidad;
    private String nombre;
    private BigDecimal precioUnitario;
}
