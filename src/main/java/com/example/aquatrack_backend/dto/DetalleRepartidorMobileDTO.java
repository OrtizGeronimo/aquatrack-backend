package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRepartidorMobileDTO {
    private String nombre;
    private String apellido;
    private String numTelefono;
    private Integer legajo;
    private String empresa;
    private String direccionEmail;
}