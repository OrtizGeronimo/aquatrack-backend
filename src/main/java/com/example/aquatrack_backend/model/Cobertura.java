package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cobertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Empresa empresa;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cobertura")
    private List<Ubicacion> ubicaciones;
}