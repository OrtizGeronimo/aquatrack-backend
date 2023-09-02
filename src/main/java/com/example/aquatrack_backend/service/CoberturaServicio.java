package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class CoberturaServicio extends ServicioBaseImpl<Cobertura>{

    @Autowired
    private CoberturaRepo coberturaRepo;

    public CoberturaServicio(RepoBase<Cobertura> repoBase) {
        super(repoBase);
    }
}
