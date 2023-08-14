package com.example.aquatrack_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEstadoPago")
    private EstadoPago estadoPago;

}