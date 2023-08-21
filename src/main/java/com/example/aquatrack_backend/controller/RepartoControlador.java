package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.RepartoServicio;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador{

    @Autowired
    private RepartoServicio repartoServicio;
}
