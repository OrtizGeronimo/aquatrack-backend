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
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private Integer numTelefono;
    private String email;
    private String url;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaCreacion;
    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaFinVigencia;
    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime horaActualizacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
    private List<CodigoTemporal> codigos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
    private List<Empleado> empleados;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
    private List<EmpresaCliente> empresaClientes;

    @OneToOne
    private Cobertura cobertura;

    @OneToOne
    private Ubicacion ubicacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "empresa")
    private List<Rol> roles;


}
