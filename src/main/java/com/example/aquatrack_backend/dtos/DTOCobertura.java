package com.example.aquatrack_backend.dtos;

import com.example.aquatrack_backend.model.Ubicacion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DTOCobertura {

    private Long id;
    private String nombreEmpresa;
    private List<DTOUbicacion> ubicacions;
}
