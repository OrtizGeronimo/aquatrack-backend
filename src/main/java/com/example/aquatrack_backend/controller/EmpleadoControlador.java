package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.EmpleadoServicio;

@RestController
@RequestMapping(path = "/empleados")
public class EmpleadoControlador{
    @Autowired
    private EmpleadoServicio empleadoServicio;

    @GetMapping(value= "")
    @PreAuthorize("hasAuthority('LISTAR_EMPLEADOS')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
    return ResponseEntity.ok().body(empleadoServicio.findAllByEnterprise(page, size, nombre, mostrar_inactivos));
    }
}
