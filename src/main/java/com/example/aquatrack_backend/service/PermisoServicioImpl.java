package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Permiso;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.PermisoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermisoServicioImpl extends ServicioBaseImpl<Permiso> implements ServicioBase<Permiso>{
    @Autowired
    private PermisoRepo permisoRepo;

    public PermisoServicioImpl(RepoBase<Permiso> repoBase) {
        super(repoBase);
    }

}
