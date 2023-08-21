package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.RolServicio;

@RestController
@RequestMapping(path = "/roles")
public class RolControlador {
  @Autowired
  private RolServicio rolServicio;

  @GetMapping(value = "")
  public ResponseEntity<?> getAll()  {
    return ResponseEntity.ok().body(rolServicio.findAll());
  }
}
