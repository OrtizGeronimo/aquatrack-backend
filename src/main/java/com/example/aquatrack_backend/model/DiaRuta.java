package com.example.aquatrack_backend.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DiaRuta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne()
  private Ruta ruta;

  @ManyToOne()
  private DiaSemana diaSemana;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diaRuta")
  List<DiaDomicilio> diaDomicilios;
}
