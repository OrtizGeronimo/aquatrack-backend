package com.example.aquatrack_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DomicilioDTO {
    private String calle;
    private Integer numero;
    private String pisoDepartamento;
    private String observaciones;
    private Long idCliente;
}
