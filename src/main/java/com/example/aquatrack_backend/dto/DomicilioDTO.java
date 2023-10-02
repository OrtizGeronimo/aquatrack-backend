package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DomicilioDTO {

    private Long id;
    private String calle;
    private Integer numero;
    private String pisoDepartamento;
    private String observaciones;
    private String domicilio; //string con todo lo anterior concatenado
    private String nombreApellidoCliente;

    private UbicacionDTO ubicacion;
}
