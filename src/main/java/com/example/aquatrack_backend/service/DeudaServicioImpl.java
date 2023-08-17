package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Deuda;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.DeudaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeudaServicioImpl extends ServicioBaseImpl<Deuda> implements ServicioBase<Deuda> {
    @Autowired
    private DeudaRepo repo;

    public DeudaServicioImpl(RepoBase<Deuda> repoBase) {
        super(repoBase);
    }
}
