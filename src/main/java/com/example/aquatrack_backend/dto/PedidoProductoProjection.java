package com.example.aquatrack_backend.dto;

import java.math.BigDecimal;

public interface PedidoProductoProjection {
    public Long getId();

    public String getNombre();

    public BigDecimal getPrecio();

    public Integer getCantidad();
}
