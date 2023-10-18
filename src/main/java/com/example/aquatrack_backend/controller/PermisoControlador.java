package com.example.aquatrack_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.PermisoDTO;
import com.example.aquatrack_backend.model.Permiso;
import com.example.aquatrack_backend.service.PermisoServicio;

@RestController
@RequestMapping(path = "/permisos")
public class PermisoControlador {

  @Autowired
  private PermisoServicio permisoServicio;

  @GetMapping(value = "")
  public ResponseEntity<?> findAll() {
    return ResponseEntity.ok().body(permisoServicio.findAll());
  }
}
