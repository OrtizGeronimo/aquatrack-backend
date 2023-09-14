package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PedidoExtraordinario extends Pedido {

  @DateTimeFormat(pattern = "dd-MM-YYYY")
  private LocalDateTime fechaCoordinadaEntrega;

  @ManyToOne()
  private TipoPedido tipoPedido;

  @ManyToOne()
  private EstadoPedido estadoPedido;
}
