package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.DeudaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeudaControlador {

    @Autowired
    private DeudaServicio service;

}
