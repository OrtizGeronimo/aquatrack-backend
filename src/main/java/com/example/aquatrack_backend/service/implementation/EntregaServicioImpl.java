package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.EntregaRepo;
import com.example.aquatrack_backend.repo.EstadoEntregaRepo;
import com.example.aquatrack_backend.service.EntregaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregaServicioImpl implements EntregaServicio {

    @Autowired
    private EntregaRepo entregaRepo;
    private EstadoEntregaRepo estadoEntregaRepo;
}
