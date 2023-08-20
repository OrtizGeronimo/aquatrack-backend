package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.service.ClienteServicioImpl;

@RestController
@RequestMapping(path = "/clientes")
public class ClienteControlador extends ControladorBaseImpl<Cliente, ClienteServicioImpl>{

    @Autowired
    private ClienteServicioImpl clienteServicio;
}