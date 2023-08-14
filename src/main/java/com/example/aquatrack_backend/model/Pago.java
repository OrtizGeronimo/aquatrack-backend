package com.example.aquatrack_backend.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaPago;

    private float total;

    @ManyToOne
    private EstadoPago estadoPago;

    @ManyToOne
    private MedioPago medioPago;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pago")
    private List<DeudaPago> deudaPagos;

    @OneToOne(cascade = CascadeType.ALL)
    private Entrega entrega;
}