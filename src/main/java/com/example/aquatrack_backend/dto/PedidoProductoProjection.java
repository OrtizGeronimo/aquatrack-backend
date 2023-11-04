package com.example.aquatrack_backend.dto;

import java.math.BigDecimal;

public interface PedidoProductoProjection {
    public Long getId();

    public String getNombre();

    public Boolean getRetornable();

    public BigDecimal getPrecio();

    public Integer getCantidad();
}
