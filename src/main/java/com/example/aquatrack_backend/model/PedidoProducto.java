package com.example.aquatrack_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PedidoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    @ManyToOne
    @JoinColumn(name = "idPedido")
    private PedidoExtraordinario pedidoExtraordinario;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
}
