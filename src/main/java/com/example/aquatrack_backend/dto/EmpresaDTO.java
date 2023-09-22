package com.example.aquatrack_backend.dto;

import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.model.Ubicacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String numTelefono;
    private String urlEmpresa;
    private String direccionEmail;
    private Ubicacion ubicacion;
    private List<RolDTO> roles;
}
