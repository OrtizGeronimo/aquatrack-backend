package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DeudaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float montoAdeudadoPago;

    @ManyToOne
    @JoinColumn(name = "idDeuda")
    private Deuda deuda;

    @ManyToOne
    @JoinColumn(name = "idPago")
    private Pago pago;

}
