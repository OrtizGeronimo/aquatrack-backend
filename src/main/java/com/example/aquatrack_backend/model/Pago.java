package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaPago;

  private float total;

  @ManyToOne(fetch = FetchType.LAZY)
  private EstadoPago estadoPago;

  @ManyToOne(fetch = FetchType.LAZY)
  private MedioPago medioPago;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pago", fetch = FetchType.LAZY)
  private List<DeudaPago> deudaPagos;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Entrega entrega;
}