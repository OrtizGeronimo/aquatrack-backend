package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepartoServicioImpl extends ServicioBaseImpl<Reparto> implements ServicioBase<Reparto> {

    @Autowired
    private RepartoRepo repo;
    public RepartoServicioImpl(RepoBase<Reparto> repoBase) {
        super(repoBase);
    }
}
