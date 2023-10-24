package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DeudaPagoDTO {

    private Long id;
    private BigDecimal monto;
    private Long pago;
    private LocalDateTime fechaRegistro;
}
