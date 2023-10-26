package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reparto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaEjecucion;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraInicio;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraFin;

    private String observaciones;

    @ManyToOne()
    private EstadoReparto estadoReparto;

    @ManyToOne()
    private Empleado repartidor;

    @OneToOne(cascade = CascadeType.ALL)
    private Ubicacion ubicacion;

    @ManyToOne()
    private Ruta ruta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reparto")
    private List<Entrega> entregas;
}