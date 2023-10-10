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

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
public class Pedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  private Domicilio domicilio;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pedido")
  private List<PedidoProducto> pedidoProductos;

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaCoordinadaEntrega;

  @ManyToOne()
  private TipoPedido tipoPedido;

  @ManyToOne()
  private EstadoPedido estadoPedido;
}