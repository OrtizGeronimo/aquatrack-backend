package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EstadoEntrega {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombreEstadoEntrega;

  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaFinVigencia;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "estadoEntrega")
  private List<Entrega> entregas;
}
