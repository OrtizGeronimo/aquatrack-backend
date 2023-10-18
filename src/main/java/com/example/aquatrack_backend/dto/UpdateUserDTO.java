package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {

    private String nombre;
    private String apellido;
    private String nroTelefono;
    // private String domicilio;
    private String email;

}
