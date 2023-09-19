package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RepartoDTO {

    private Long id;
    private List<EntregaDTO> entregas;
    private EstadoRepartoDTO estadoReparto;

}
