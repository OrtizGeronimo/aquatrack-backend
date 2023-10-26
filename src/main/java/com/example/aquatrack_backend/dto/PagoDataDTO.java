package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PagoDataDTO {
    private BigDecimal monto;
    private Long idMedioPago;
}
