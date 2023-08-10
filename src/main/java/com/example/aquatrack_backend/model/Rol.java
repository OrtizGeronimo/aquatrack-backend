package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Boolean activo;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaFinVigencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PermisoRol> permisos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rol")
    private List<RolUsuario> rolUsuario;

    @ManyToOne
    private Empresa empresa;

}
