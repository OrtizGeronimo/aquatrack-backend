package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.service.CoberturaServicio;

@RestController
@RequestMapping(path = "/coberturas")
public class CoberturaControlador{
    @Autowired
    private CoberturaServicio coberturaServicio;
}
