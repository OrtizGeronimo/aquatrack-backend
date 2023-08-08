package com.example.aquatrack_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEstadoReparto")
    private EstadoReparto estadoReparto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEmpleado")
    private Empleado empleado;

    @ManyToOne(optional = true)
    @JoinColumn(name = "idRuta")
    private Ruta ruta;

}