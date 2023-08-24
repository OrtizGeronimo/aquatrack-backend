package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.CrearRolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.RolServicio;

@RestController
@RequestMapping(path = "/roles")
public class RolControlador {
  @Autowired
  private RolServicio rolServicio;

  @GetMapping(value = "")
  public ResponseEntity<?> findAll() {
    return ResponseEntity.ok().body(rolServicio.findAll());
  }

  @PostMapping(value = "")
  public ResponseEntity<?> create(@RequestBody CrearRolDTO rol) {
    return ResponseEntity.ok().body(rolServicio.createRol(rol));
  }

  @GetMapping(value = "/{id}/permisos")
  public ResponseEntity<?> findAllPermissionsByRole(@PathVariable Long id) throws RecordNotFoundException {
    return ResponseEntity.ok().body(rolServicio.findAllPermissionsByRole(id));
  }
}
