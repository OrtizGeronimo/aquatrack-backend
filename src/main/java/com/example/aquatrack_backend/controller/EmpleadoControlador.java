package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.service.ClienteServicioImpl;
import com.example.aquatrack_backend.service.EmpleadoServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/empleados")
public class EmpleadoControlador extends ControladorBaseImpl<Empleado, EmpleadoServicioImpl>{
    @Autowired
    private EmpleadoServicioImpl empleadoServicio;
}
