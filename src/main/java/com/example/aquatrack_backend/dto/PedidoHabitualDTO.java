package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PedidoHabitualDTO {
    private Long id;
    private List<PedidoHabitualProductoDTO> productos;
}
