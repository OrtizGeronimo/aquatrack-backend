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
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Domicilio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String descripcion;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaFinVigencia;

  @OneToOne
  private Cliente cliente;

  @OneToOne
  private Ubicacion ubicacion;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio", fetch = FetchType.LAZY)
  private List<DomicilioProducto> productoDomicilios;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio", fetch = FetchType.LAZY)
  private List<Pedido> pedidos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio", fetch = FetchType.LAZY)
  private List<Entrega> entregas;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio", fetch = FetchType.LAZY)
  private List<DiaDomicilio> diaDomicilios;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio", fetch = FetchType.LAZY)
  private List<DomicilioRuta> domicilioRutas;

  @OneToOne(fetch = FetchType.LAZY)
  private Deuda deuda;
}
