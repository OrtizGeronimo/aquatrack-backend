package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

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

//    private List<ProductoDomicilio> productoDomicilios;
//    private List<Pedido> pedidos;
//    private List<Entrega> entregas;
//    diaDomicilio
//    domicilioRuta
//    deuda

}
