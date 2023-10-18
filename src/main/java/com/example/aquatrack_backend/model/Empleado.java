package com.example.aquatrack_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
// @TableGenerator(name = "employee_gen", table = "sequence_generator", pkColumnName = "sequence_name", valueColumnName = "next_val", allocationSize = 1)
public class Empleado extends Persona {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer legajo;

  private LocalDate fechaIngreso;
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
