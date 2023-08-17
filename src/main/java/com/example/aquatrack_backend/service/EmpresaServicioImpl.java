package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServicioImpl extends ServicioBaseImpl<Empresa> implements ServicioBase<Empresa>{
    @Autowired
    private EmpresaRepo empresaRepo;

    public EmpresaServicioImpl(RepoBase<Empresa> repoBase) {
        super(repoBase);
    }
}
