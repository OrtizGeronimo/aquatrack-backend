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

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Entrega {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
  private LocalDateTime fechaHoraVisita;

  @ManyToOne()
  private Domicilio domicilio;

  @ManyToOne()
  private Reparto reparto;

  @ManyToOne()
  private EstadoEntrega estadoEntrega;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "entrega")
  private List<EntregaDetalle> entregaDetalles;

  @OneToOne(cascade = CascadeType.ALL)
  private Pago pago;

  private Integer ordenVisita;
}
