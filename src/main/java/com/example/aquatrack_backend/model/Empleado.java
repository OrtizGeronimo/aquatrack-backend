package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Empleado extends Persona {

    private Integer legajo;

    private LocalDate fechaIngreso;
    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaInicioVacaciones;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaFinVacaciones;

    @ManyToOne()
    private TipoEmpleado tipo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "repartidor")
    private List<Reparto> reparto;

    @OneToMany(mappedBy = "empleado")
    private List<Pago> pagosRegistrados;
}
