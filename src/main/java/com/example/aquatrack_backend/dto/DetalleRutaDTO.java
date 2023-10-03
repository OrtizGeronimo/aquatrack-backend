package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DetalleRutaDTO {

    private Long id;
    private String nombre;
    private List<DomicilioDetalleDTO> domicilios;

}
