package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoberturaServicioImpl extends ServicioBaseImpl<Cobertura> implements ServicioBase<Cobertura>{

    @Autowired
    private CoberturaRepo coberturaRepo;

    public CoberturaServicioImpl(RepoBase<Cobertura> repoBase) {
        super(repoBase);
    }

}
