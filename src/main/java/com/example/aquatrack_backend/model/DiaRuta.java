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
public class DiaRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Ruta ruta;

    @ManyToOne
    private DiaSemana diaSemana;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diaRuta")
    List<DiaDomicilio> diaDomicilios;
}
