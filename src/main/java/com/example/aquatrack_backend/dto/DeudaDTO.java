package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeudaDTO {

    private Long id;
    private BigDecimal monto;
    private LocalDateTime fechaUltimaActualizacion;
    private List<DeudaPagoDTO> cambios;
}
