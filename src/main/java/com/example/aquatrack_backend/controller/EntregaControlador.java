package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.EntregaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntregaControlador {

    @Autowired
    private EntregaServicio entregaServicio;
}