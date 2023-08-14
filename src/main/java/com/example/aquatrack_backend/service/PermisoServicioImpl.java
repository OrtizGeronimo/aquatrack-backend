package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.PermisoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermisoServicioImpl implements ServicioBase{
    @Autowired
    private PermisoRepo permisoRepo;
}
