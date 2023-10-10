package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @ManyToOne()
  private Empresa empresa;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<Reparto> repartos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<DiaRuta> diaRutas;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<DomicilioRuta> domicilioRutas;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaFinVigencia;
}