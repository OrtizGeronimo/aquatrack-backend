package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.ModificarRolDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.dto.CrearRolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.RolServicio;

@RestController
@RequestMapping(path = "/roles")
public class RolControlador {
    @Autowired
    private RolServicio rolServicio;

    @GetMapping(value = "")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok().body(rolServicio.findAll(page, size, nombre, mostrar_inactivos));
    }

    @PostMapping(value = "")
    public ResponseEntity<?> create(@RequestBody CrearRolDTO rol) {
        return ResponseEntity.ok().body(rolServicio.createRol(rol));
    }

    @GetMapping(value = "/{id}/permisos")
    public ResponseEntity<?> findAllPermissionsByRole(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rolServicio.findAllPermissionsByRole(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ModificarRolDTO rol) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rolServicio.update(id, rol));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> disable(@PathVariable Long id) throws RecordNotFoundException {
        rolServicio.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/enable")
    public ResponseEntity<?> enable(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rolServicio.enable(id));
    }
}
