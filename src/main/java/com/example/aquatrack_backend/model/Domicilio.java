package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaFinVigencia;

    @OneToOne
    private Cliente cliente;

    @OneToOne
    private Ubicacion ubicacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio")
    private List<DomicilioProducto> productoDomicilios;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio")
    private List<Pedido> pedidos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio")
    private List<Entrega> entregas;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio")
    private List<DiaDomicilio> diaDomicilios;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "domicilio")
    private List<DomicilioRuta> domicilioRutas;

    @OneToOne
    private Deuda deuda;
}
