package com.example.aquatrack_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(pattern = "dd-MM-YYYY HH:mm:ss")
    private LocalDateTime fechaHoraVisita;

/*  @ManyToOne(optional = false)
    @JoinColumn(name = "idDomicilio")
    private Domicilio domicilio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idReparto")
    private Reparto reparto;*/

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEstadoEntrega")
    private EstadoEntrega estadoEntrega;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "entrega")
    private List<EntregaDetalle> entregaDetalles;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPago")
    private Pago pago;*/
}