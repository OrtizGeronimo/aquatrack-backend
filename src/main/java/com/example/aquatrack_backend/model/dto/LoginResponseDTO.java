package com.example.aquatrack_backend.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {

    private String usuario;
    private String contraseña;
    private List<String> roles;
    private String token;
}
