package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.RolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolServicioImpl implements ServicioBase{
    @Autowired
    private RolRepo rolRepo;
}
