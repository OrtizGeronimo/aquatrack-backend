package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.EntregaServicioImpl;
import com.example.aquatrack_backend.service.ServicioBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntregaControlador {

    @Autowired
    private EntregaServicioImpl entregaServicio;
}