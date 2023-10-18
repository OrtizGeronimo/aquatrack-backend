package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;

import javax.persistence.*;

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

  @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
  private LocalDateTime fechaFinVigencia;

  @ManyToOne()
  private Empresa empresa;

  @OneToOne()
  private Usuario usuario;
}