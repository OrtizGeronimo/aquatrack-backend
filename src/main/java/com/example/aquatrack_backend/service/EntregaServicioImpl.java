package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EntregaRepo;
import com.example.aquatrack_backend.repo.EstadoEntregaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.service.ServicioBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregaServicioImpl extends ServicioBaseImpl<Entrega> implements ServicioBase<Entrega> {

    @Autowired
    private EntregaRepo entregaRepo;
    private EstadoEntregaRepo estadoEntregaRepo;

    public EntregaServicioImpl(RepoBase<Entrega> repoBase) {
        super(repoBase);
    }
}
