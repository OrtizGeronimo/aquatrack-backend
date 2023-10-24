package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class EditarRepartidorMobileDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe estar formado por 10 caracteres numéricos")
    private String numTelefono;
}
