package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequestDTO {
    private String direccionEmail;
    private String contraseña;
    private String confirmacionContraseña;
    private Long idEmpresa;
}
