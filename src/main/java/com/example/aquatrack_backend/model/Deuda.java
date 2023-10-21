package com.example.aquatrack_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Deuda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaUltimaActualizacion;

  private BigDecimal monto;

  private BigDecimal montoMaximo;

  @OneToOne(mappedBy = "deuda")
  private Domicilio domicilio;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "deuda")
  private List<DeudaPago> deudaPagos;

}