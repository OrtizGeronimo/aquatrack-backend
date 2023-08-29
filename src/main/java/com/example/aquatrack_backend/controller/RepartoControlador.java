package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.service.ClienteServicioImpl;
import com.example.aquatrack_backend.service.RepartoServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador extends ControladorBaseImpl<Reparto, RepartoServicioImpl>{

    @Autowired
    private RepartoServicioImpl repartoServicio;


    @GetMapping("/{id}/crear")
    public ResponseEntity<?> crearReparto(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(servicio.crearReparto(id));
    }
}
