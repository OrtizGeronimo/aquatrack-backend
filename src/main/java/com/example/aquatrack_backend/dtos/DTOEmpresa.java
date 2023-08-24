package com.example.aquatrack_backend.dtos;

import com.example.aquatrack_backend.model.Ubicacion;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DTOEmpresa {

    private String nombre;
    private String numTelefono;
    private Ubicacion ubicacion;
}
