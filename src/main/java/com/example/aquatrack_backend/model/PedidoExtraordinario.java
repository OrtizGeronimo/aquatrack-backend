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
public class PedidoExtraordinario extends Pedido{

    @DateTimeFormat(pattern = "dd-MM-YYYY")
    private LocalDateTime fechaCoordinadaEntrega;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idTipoPedido")
    private TipoPedido tipoPedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idEstadoPedido")
    private EstadoPedido estadoPedido;
}
