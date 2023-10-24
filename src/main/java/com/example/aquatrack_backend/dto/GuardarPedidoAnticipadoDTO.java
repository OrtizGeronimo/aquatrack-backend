package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardarPedidoAnticipadoDTO {
    private Long idRuta;
    private Long idDomicilio;
    private LocalDateTime fechaCoordinadaEntrega;
}
