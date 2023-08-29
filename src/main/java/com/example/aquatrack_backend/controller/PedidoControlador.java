package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.PedidoServicio;

@RestController
@RequestMapping(path = "/pedidos")
public class PedidoControlador{

    @Autowired
    private PedidoServicio pedidoServicio;
}
