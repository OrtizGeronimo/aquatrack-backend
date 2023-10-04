package com.example.aquatrack_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EstadoEntregaDTO {

    private Long id;
    private String nombreEstadoEntrega;
}
