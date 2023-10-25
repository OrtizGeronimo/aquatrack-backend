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
public class PedidoFormDTO {

    private List<String> estados;
    private List<String> tipos;
    private List<String> rutas;
    private List<ProductoDTO> productos;

}
