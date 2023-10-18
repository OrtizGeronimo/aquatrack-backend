package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DomicilioDetalleDTO {

    private Long id;
    private String domicilio;
    private String nombreCliente;
    private Double latitud;
    private Double longitud;
    private String localidad;
}
