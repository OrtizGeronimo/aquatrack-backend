package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.EntregaServicio;

@RestController
@RequestMapping(path = "/entregas")
public class EntregaControlador{

    @Autowired
    private EntregaServicio entregaServicio;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('LISTAR_CLIENTES')")
    public ResponseEntity<?> detallarEntrega(@PathVariable Long id) throws RecordNotFoundException{
        return ResponseEntity.ok().body(entregaServicio.detallarEntrega(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_ENTREGAS')")
    public ResponseEntity<?> disableEntrega(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.disableEntrega(id));
    }
}