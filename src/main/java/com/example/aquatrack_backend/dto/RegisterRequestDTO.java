package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class RegisterRequestDTO {
    @Email
    @NotBlank
    private String direccionEmail;
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$", message = "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.")
    private String contraseña;
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$", message = "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.")
    private String confirmacionContraseña;
    private Long idEmpresa;
}
