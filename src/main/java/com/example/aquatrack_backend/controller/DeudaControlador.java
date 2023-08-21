package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.model.Deuda;
import com.example.aquatrack_backend.service.DeudaServicioImpl;

@RestController
@RequestMapping(path = "/deudas")
public class DeudaControlador extends ControladorBaseImpl<Deuda, DeudaServicioImpl>{

    @Autowired
    private DeudaServicioImpl deudaServicio;

}
