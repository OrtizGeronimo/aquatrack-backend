package com.example.aquatrack_backend.dto;

import com.example.aquatrack_backend.model.Cliente;

public interface DomicilioProjection {

    Long getId();

    String getCalle();

    Integer getNumero();

    String getPisoDepartamento();

    Long getCliente();
}
