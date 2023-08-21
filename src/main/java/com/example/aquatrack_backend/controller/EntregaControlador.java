package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.EntregaServicio;

@RestController
@RequestMapping(path = "/entregas")
public class EntregaControlador{

    @Autowired
    private EntregaServicio entregaServicio;
}