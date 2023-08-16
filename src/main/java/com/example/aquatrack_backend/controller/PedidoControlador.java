package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Pedido;
import com.example.aquatrack_backend.service.ClienteServicioImpl;
import com.example.aquatrack_backend.service.PedidoServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/pedidos")
public class PedidoControlador extends ControladorBaseImpl<Pedido, PedidoServicioImpl>{

    @Autowired
    private PedidoServicioImpl pedidoServicio;
}
