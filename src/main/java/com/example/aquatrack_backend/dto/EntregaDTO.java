package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntregaDTO {

    private Long id;
    private DomicilioDTO domicilio;
    private EstadoEntregaDTO estadoEntrega;
    private Integer ordenVisita;

}
