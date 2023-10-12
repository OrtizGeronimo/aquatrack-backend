package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardarPedidoDTO {
    private Long idEstado;
    private Long idTipo;
    private LocalDateTime fechaCoordinadaEntrega;
    private Long idDomicilio;
    private List<PedidoProductoDTO> pedidoProductos;
}