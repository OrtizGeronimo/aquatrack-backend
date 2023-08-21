package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @ManyToOne()
  private Empresa empresa;

  @ManyToOne()
  private TipoEmpleado tipo;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "repartidor")
  private List<Reparto> reparto;
}
