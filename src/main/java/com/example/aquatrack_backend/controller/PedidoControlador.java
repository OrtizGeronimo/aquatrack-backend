package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.PedidoServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PedidoControlador {

    @Autowired
    private PedidoServicioImpl pedidoServicio;
}
