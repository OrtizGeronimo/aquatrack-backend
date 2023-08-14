package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.DeudaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeudaServicioImpl implements ServicioBase {
    @Autowired
    private DeudaRepo repo;
}
