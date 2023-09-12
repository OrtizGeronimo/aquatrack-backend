package com.example.aquatrack_backend.dto;

import com.example.aquatrack_backend.model.Rol;
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
    private List<RolDTO> roles;
}
