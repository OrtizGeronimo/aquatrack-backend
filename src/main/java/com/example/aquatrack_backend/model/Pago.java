package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaFinVigencia;

    private BigDecimal total;

    @ManyToOne()
    private EstadoPago estadoPago;

    @ManyToOne()
    private MedioPago medioPago;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pago")
//    private List<DeudaPago> deudaPagos;

    @OneToOne(mappedBy = "pago")
    private DeudaPago deudaPago;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "pago")
    private Entrega entrega;

    @ManyToOne
    private Empleado empleado;
}