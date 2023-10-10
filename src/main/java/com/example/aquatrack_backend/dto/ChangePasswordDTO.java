package com.example.aquatrack_backend.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordDTO {
    
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$", message = "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.")
    private String formerPassword;
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).*$", message = "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.")
    private String password;
}
