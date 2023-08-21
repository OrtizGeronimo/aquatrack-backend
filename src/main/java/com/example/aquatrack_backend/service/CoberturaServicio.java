package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.repo.CoberturaRepo;

@Service
public class CoberturaServicio extends ServicioBase{

    @Autowired
    private CoberturaRepo coberturaRepo;

}
