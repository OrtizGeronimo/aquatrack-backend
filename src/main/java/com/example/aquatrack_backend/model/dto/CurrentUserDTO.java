package com.example.aquatrack_backend.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrentUserDTO {

    private String nombre;
    private String empresa;
    private List<String> permisos;
}
