package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.EmpleadoServicio;

@RestController
@RequestMapping(path = "/empleados")
public class EmpleadoControlador{
    @Autowired
    private EmpleadoServicio empleadoServicio;
}
