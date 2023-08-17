package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.service.ClienteServicioImpl;
import com.example.aquatrack_backend.service.EntregaServicioImpl;
import com.example.aquatrack_backend.service.ServicioBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/entregas")
public class EntregaControlador extends ControladorBaseImpl<Entrega, EntregaServicioImpl>{

    @Autowired
    private EntregaServicioImpl entregaServicio;
}