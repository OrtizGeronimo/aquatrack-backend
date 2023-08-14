package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.EmpresaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServicioImpl implements ServicioBase{
    @Autowired
    private EmpresaRepo empresaRepo;
}
