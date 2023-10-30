package com.example.aquatrack_backend.model;


import java.math.BigDecimal;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeudaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal montoAdeudadoPago;

    @ManyToOne()
    private Deuda deuda;

    @OneToOne()
    private Pago pago;

}
