package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepartoServicio extends ServicioBase {

    @Autowired
    private RepartoRepo repo;
   
}
