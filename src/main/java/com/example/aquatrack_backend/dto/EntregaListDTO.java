package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaListDTO {
    private Long id;
    private LocalDateTime fechaHoraVisita;
    private Long estadoEntregaId;
    private String estadoEntrega;
    private Integer ordenVisita;
    private Double latitudDomicilio;
    private Double longitudDomicilio;
    private String cliente;
}
