package com.example.aquatrack_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {
    private String nombre;
    private String empresa;
    private String password;
    private boolean validado;
    private String rolPrincipal;
    private List<String> permisos;
    private String direccionEmail;
}
