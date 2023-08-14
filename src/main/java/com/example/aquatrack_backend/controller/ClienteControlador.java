package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.ClienteServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteControlador {

    @Autowired
    private ClienteServicioImpl clienteServicio;
}