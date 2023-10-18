package com.example.aquatrack_backend.model;

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

  @ManyToOne()
  private Ruta ruta;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reparto")
  private List<Entrega> entregas;
}