package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.CoberturaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoberturaServicioImpl implements ServicioBase{

    @Autowired
    private CoberturaRepo coberturaRepo;
}
