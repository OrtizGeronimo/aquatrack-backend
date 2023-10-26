package com.example.aquatrack_backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaFinVigencia;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDate fechaCoordinadaEntrega;

    @ManyToOne()
    private TipoPedido tipoPedido;

    @ManyToOne()
    private EstadoPedido estadoPedido;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pedido")
    private List<EntregaPedido> entregaPedidos;
}