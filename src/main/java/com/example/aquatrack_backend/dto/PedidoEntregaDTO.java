package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PedidoEntregaDTO {
    private Long id;
    private String tipo;
    private List<PedidoProductoDTO> pedidoProductos;
}
