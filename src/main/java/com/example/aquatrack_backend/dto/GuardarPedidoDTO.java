package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardarPedidoDTO {
    private String tipo;
    private LocalDate fechaCoordinadaEntrega;
    private Long idDomicilio;
    private List<PedidoProductoDTO> pedidoProductos;
    private Long idRuta; //Caso de pedido extraordinario
}
