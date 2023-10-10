package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoListDTO {

    private Long id;
    private LocalDateTime fechaCoordinadaEntrega;
    private DomicilioDTO domicilio;
    private List<PedidoProductoDTO> pedidoProductos;
}
