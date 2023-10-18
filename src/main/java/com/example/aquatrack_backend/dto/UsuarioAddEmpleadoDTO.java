package com.example.aquatrack_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioAddEmpleadoDTO {

    private Long id;

    private String direccionEmail;
    private String contrasenia;
    private Boolean validado;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaFinVigencia;

    private List<Long> roles;
}
