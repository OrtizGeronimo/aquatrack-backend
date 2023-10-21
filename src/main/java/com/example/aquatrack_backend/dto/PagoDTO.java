package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PagoDTO {

    private Long id;
    private BigDecimal total;
    private String medioPago;
    private LocalDateTime fechaPago;

}
