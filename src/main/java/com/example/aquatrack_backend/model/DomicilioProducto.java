package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DomicilioProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

/*  @ManyToOne
    @JoinColumn(name = "idDomicilio")
    private Domicilio domicilio;*/

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;
}
