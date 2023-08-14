package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idDomicilio")
    private Domicilio domicilio;*/

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pedido")
    private List<PedidoProducto> pedidoProductos;
}
