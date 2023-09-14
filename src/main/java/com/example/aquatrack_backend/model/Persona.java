package com.example.aquatrack_backend.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Persona {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private Long id;

  private String nombre;

  private String apellido;

  private String numTelefono;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDate fechaCreacion;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDate fechaFinVigencia;

  @OneToOne()
  private Usuario usuario;
}