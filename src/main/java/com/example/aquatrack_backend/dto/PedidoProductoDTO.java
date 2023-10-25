package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoProductoDTO {
    private Long idProducto;
    private String nombreProducto;
    @Min(value = 1, message = "La cantidad mínima de producto en un pedido debe ser una unidad.")
    private Integer cantidad;
    private BigDecimal precio;
}
