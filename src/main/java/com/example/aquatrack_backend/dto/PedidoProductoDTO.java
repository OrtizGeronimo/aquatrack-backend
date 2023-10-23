package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoProductoDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precio;
}
