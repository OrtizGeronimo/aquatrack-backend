package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reparto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaEjecucion;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaYHoraFin;

    @ManyToOne
    private EstadoReparto estadoReparto;

    @ManyToOne
    private Empleado repartidor;

    @ManyToOne
    private Ruta ruta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reparto")
    private List<Entrega> entregas;
}