package com.example.aquatrack_backend.model;

import java.math.BigDecimal;
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

  private BigDecimal total;

  @ManyToOne()
  private EstadoPago estadoPago;

  @ManyToOne()
  private MedioPago medioPago;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pago")
  private List<DeudaPago> deudaPagos;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "pago")
  private Entrega entrega;
}