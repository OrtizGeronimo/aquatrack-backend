package com.example.aquatrack_backend.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoProducto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer cantidad;

  @ManyToOne()
  private Pedido pedido;

  @ManyToOne()
  private Producto producto;

  public PedidoProducto(Integer cantidad, Pedido pedido, Producto producto){
    this.cantidad = cantidad;
    this.pedido = pedido;
    this.producto = producto;
  }
}
