package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioAddEmpleadoDTO {

    private Long id;

    private String direccionEmail;
    private String contrasenia;
    private Boolean validado;

    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaFinVigencia;

    private List<Long> roles;
}
