package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccionEmail;
    private String contraseña;
    private Boolean validado;

    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDate fechaFinVigencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario")
    private List<UsuarioCodigoValidacion> codigos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario")
    private List<RolUsuario> rolesUsuario;

    @OneToOne(mappedBy = "usuario")
    private Persona persona;

    @Transient
    private String confirmacionContraseña;
}