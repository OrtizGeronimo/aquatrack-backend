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

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;

  private String descripcion;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaFinVigencia;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto", fetch = FetchType.LAZY)
  private List<Precio> precios;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto", fetch = FetchType.LAZY)
  private List<PedidoProducto> pedidoProductos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto", fetch = FetchType.LAZY)
  private List<DomicilioProducto> domicilioProductos;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto", fetch = FetchType.LAZY)
  private List<EntregaDetalle> entregaDetalles;

  @ManyToOne(fetch = FetchType.LAZY)
  private Empresa empresa;
}
