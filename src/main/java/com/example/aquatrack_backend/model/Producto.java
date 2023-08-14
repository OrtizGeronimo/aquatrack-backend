package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto")
    private List<Precio> precios;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto")
    private List<PedidoProducto> pedidoProductos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto")
    private List<DomicilioProducto> domicilioProductos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "producto")
    private List<EntregaDetalle> entregaDetalles;
}
