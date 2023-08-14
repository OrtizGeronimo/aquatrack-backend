package com.example.aquatrack_backend.model;


import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
    private List<Reparto> repartos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
    private List<DiaRuta> diaRutas;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
    private List<DomicilioRuta> domicilioRutas;
}