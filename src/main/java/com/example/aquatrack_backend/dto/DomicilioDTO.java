package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomicilioDTO {
    private Long id;
    private String calle;
    private Integer numero;
    private String pisoDepartamento;
    private String observaciones;
    private Long idCliente;
    private String domicilio; //string con
    private String nombreApellidoCliente;

    private UbicacionDTO ubicacion;
}
