package com.example.aquatrack_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.service.RolServicio;

@RestController
@RequestMapping(path = "/roles")
public class RolControlador {
  @Autowired
  private RolServicio rolServicio;

  @GetMapping(value = "")
  public ResponseEntity<?> findAll() {
    List<Rol> roles = rolServicio.findAll();
    return ResponseEntity.ok().body(roles.stream()
                                         .map(rol -> new ModelMapper().map(rol, RolDTO.class))
                                         .collect(Collectors.toList()));
  }
}
