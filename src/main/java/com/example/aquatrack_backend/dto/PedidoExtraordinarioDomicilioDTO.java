package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoExtraordinarioDomicilioDTO {
    private Long id;

    private String cliente;

    private List<Long> diaDomicilios;
}
