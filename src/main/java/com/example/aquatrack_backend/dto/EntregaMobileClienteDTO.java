package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntregaMobileClienteDTO {
    private Long id;
    private String repartidor;
    private LocalDateTime fechaHoraVisita;
    private EntregaMobileClientePagoDTO pago;
    private List<PedidoProductoDTO> productosEntregados;
    private BigDecimal montoEntrega;
}
