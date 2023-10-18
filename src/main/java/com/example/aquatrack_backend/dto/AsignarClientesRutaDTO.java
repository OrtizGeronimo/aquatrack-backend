package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AsignarClientesRutaDTO {

    private GuardarRutaDTO ruta;
    private List<DomicilioDetalleDTO> domicilios;
}
