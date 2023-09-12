package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.service.EmpresaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/empresas")
@CrossOrigin(origins = "*")
public class EmpresaControlador{

    @Autowired
    EmpresaServicio empresaServicio;
}