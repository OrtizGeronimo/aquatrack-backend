package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Ruta;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RutaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RutaServicioImpl extends ServicioBaseImpl<Ruta> implements ServicioBase<Ruta>{

    @Autowired
    private RutaRepo repo;
    public RutaServicioImpl(RepoBase<Ruta> repoBase) {
        super(repoBase);
    }
}
