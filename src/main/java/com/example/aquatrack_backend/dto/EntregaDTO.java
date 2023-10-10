package com.example.aquatrack_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaDTO {

    private Long id;
    private DomicilioDTO domicilio;
    private EstadoEntregaDTO estadoEntrega;
    private Integer ordenVisita;

}
