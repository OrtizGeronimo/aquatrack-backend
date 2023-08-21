package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServicio extends ServicioBase{
    @Autowired
    private EmpresaRepo empresaRepo;

}
