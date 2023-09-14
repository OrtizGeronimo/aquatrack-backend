package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.RepartoServicio;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador{

    @Autowired
    private RepartoServicioImpl repartoServicio;


    @GetMapping("/{id}/crear")
    public ResponseEntity<?> crearReparto(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(servicio.crearReparto(id));
    }
    private RepartoServicio repartoServicio;
}
