package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.GuardarRutaDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.RutaServicio;

@RestController
@RequestMapping(path = "/rutas")
public class RutaControlador{

    @Autowired
    private RutaServicio rutaServicio;


    @GetMapping("/nuevo")
    public ResponseEntity<?> rellenarForm(){
        return ResponseEntity.ok().body(rutaServicio.newRuta());
    }

    @PostMapping("")
    public ResponseEntity<?> crearRuta(@RequestBody GuardarRutaDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.crearRuta(dto));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LISTAR_RUTAS')")
    public ResponseEntity<?> detalle(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.detalleRuta(id));
    }

}
