package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuardarClienteWebProductoDTO {
    private Long idProducto;
    private Integer cantidad;
}
