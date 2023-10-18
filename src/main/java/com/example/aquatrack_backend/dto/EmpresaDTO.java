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
    private String nombreEmpresa;
    private String numTelefono;
    private String urlEmpresa;
    private String mailEmpresa;
    private UbicacionDTO ubicacion;
    private Integer hora;
    private Integer minuto;
    private Double latitud;
    private Double longitud;
    private List<RolDTO> roles;
}
