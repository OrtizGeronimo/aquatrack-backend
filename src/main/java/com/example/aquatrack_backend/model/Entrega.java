package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ordenVisita;

    private String observaciones;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraVisita;

    @ManyToOne()
    private Domicilio domicilio;

    @ManyToOne()
    private Reparto reparto;

    @ManyToOne()
    private EstadoEntrega estadoEntrega;

    //monto de lo que fue entregado, debería ser nulo hasta que se PROCESE LA ENTREGA
    private BigDecimal monto;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entrega")
    private List<EntregaDetalle> entregaDetalles;

    @OneToOne(cascade = CascadeType.ALL)
    private Pago pago;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "entrega")
    private List<EntregaPedido> entregaPedidos;
}
