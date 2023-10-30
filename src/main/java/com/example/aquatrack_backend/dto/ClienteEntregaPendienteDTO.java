package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntregaPendienteDTO {
    private Boolean tieneEntrega;
    private String repartidor;
    private Long idReparto;
}
