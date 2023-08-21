package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServicio extends ServicioBase{
    @Autowired
    private EmpleadoRepo empleadoRepo;

}
