package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TableGenerator(name = "employee_gen", table = "sequence_generator", pkColumnName = "sequence_name", valueColumnName = "next_val", allocationSize = 1)
public class Empleado extends Persona {

    private Integer legajo;
    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaIngreso;
    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaInicioVacaciones;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaFinVacaciones;

    @ManyToOne
    private Empresa empresa;

    @ManyToOne
    private TipoEmpleado tipo;

    //private List<Reparto> reparto

}
