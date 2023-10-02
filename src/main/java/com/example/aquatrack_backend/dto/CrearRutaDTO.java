package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CrearRutaDTO {
    private List<Long> domicilios;
    private List<Long> dias;
    private Long repartidorId;
    private String nombre;
}
