package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitud;
    private Double longitud;

    @OneToOne(mappedBy = "ubicacion")
    private Domicilio domicilio;

    @OneToOne(mappedBy = "ubicacion")
    private Empresa empresa;

    @ManyToOne()
    private Cobertura cobertura;
}