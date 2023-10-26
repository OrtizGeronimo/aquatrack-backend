package com.example.aquatrack_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoBusquedaDTO {
    private List<ObjetoGenericoDTO> estados;
    private List<ObjetoGenericoDTO> tipos;
}
