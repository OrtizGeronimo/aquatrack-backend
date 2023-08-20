package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.service.CoberturaServicioImpl;

@RestController
@RequestMapping(path = "/coberturas")
public class CoberturaControlador extends ControladorBaseImpl<Cobertura, CoberturaServicioImpl>{
    @Autowired
    private CoberturaServicioImpl coberturaServicio;
}
