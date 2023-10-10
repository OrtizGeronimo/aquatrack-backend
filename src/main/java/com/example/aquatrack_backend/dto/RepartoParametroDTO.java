package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RepartoParametroDTO {

    private List<ObjetoGenericoDTO> repartidores;
    private List<ObjetoGenericoDTO> rutas;
    private List<ObjetoGenericoDTO> estados;

}
