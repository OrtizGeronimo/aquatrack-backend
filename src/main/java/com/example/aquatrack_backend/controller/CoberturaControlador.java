package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.CoberturaServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoberturaControlador {
    @Autowired
    private CoberturaServicioImpl coberturaServicio;
}
