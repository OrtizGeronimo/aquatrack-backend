package com.example.aquatrack_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EntregaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidadEntregada;

    private Integer cantidadRecibida;

    @ManyToOne
    @JoinColumn(name = "idEntrega")
    private Entrega entrega;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
}
