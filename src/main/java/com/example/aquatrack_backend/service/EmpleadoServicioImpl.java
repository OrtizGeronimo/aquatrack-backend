package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServicioImpl extends ServicioBaseImpl<Empleado> implements ServicioBase<Empleado>{
    @Autowired
    private EmpleadoRepo empleadoRepo;

    public EmpleadoServicioImpl(RepoBase<Empleado> repoBase) {
        super(repoBase);
    }
}
