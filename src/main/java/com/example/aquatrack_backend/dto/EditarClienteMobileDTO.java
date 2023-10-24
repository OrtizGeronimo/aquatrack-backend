package com.example.aquatrack_backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditarClienteMobileDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe estar formado por 10 caracteres numéricos")
    private String numTelefono;
}
