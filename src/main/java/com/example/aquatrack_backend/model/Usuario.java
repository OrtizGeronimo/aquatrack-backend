package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
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
    private LocalDateTime fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaFinVigencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<UsuarioCodigoValidacion> codigos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<RolUsuario> rolesUsuario;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Persona persona;

    @Transient
    private String confirmacionContraseña;

}