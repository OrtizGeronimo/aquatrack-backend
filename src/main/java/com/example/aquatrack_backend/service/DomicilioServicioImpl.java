package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.DomicilioRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomicilioServicioImpl extends ServicioBaseImpl<Domicilio> implements ServicioBase<Domicilio>{
    @Autowired
    private DomicilioRepo domicilioRepo;

    public DomicilioServicioImpl(RepoBase<Domicilio> repoBase) {
        super(repoBase);
    }
}
