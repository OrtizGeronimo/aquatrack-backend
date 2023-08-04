package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.RutaRepo;
import com.example.aquatrack_backend.service.RutaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RutaServicioImpl implements RutaServicio {

    @Autowired
    private RutaRepo repo;

}
