package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarEntregaDTO {
    private String observaciones;

    private PagoDataDTO pago;

    private List<PedidoEntregadoDTO> pedidosEntregados;

    private List<PedidoEntregaDevueltoDTO> productosDevueltos;
}
