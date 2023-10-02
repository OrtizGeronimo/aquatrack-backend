package com.example.aquatrack_backend.dto;

import com.example.aquatrack_backend.model.DiaSemana;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Empleado;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RutaFormDTO {

    private List<EmpleadoDTO> repartidores;
    private List<DomicilioDTO> domicilios;
    private List<DiaSemanaDTO> dias;

}
