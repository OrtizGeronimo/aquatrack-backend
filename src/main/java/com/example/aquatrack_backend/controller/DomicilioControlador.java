package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.service.ClienteServicioImpl;
import com.example.aquatrack_backend.service.DomicilioServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/domicilios")
public class DomicilioControlador extends ControladorBaseImpl<Domicilio, DomicilioServicioImpl>{
    @Autowired
    private DomicilioServicioImpl domicilioServicio;
}
