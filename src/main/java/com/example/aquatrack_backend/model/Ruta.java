package com.example.aquatrack_backend.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ruta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<Reparto> repartos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<DiaRuta> diaRutas;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ruta")
  private List<DomicilioRuta> domicilioRutas;
}