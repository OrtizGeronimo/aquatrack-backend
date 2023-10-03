package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DomicilioDetalleDTO {

    private String domicilio;
    private Double latitud;
    private Double longitud;
}
