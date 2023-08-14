package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.RutaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RutaServicioImpl implements ServicioBase{

    @Autowired
    private RutaRepo repo;
}
