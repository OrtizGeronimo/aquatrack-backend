package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.PagoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServicioImpl implements ServicioBase {

    @Autowired
    private PagoRepo repo;
}