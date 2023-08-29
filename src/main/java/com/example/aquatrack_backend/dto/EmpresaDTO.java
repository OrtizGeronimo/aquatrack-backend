package com.example.aquatrack_backend.dto;

import com.example.aquatrack_backend.model.Ubicacion;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmpresaDTO {

    private String nombre;
    private String numTelefono;
    private Ubicacion ubicacion;
}
