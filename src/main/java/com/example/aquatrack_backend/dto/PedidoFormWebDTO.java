package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoFormWebDTO {

    private List<ObjetoGenericoDTO> productos;
    private List<ObjetoGenericoDTO> clientes;
}
