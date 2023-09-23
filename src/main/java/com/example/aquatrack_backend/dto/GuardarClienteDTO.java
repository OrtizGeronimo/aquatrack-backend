package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardarClienteDTO {
    private String nombre;
    private String apellido;
    private Integer dni;
    private String numTelefono;
    private String mail;
    private String password;
}
