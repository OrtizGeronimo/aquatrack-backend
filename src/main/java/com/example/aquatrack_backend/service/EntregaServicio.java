package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Entrega;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.EntregaRepo;
import com.example.aquatrack_backend.repo.EstadoEntregaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.service.ServicioBaseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregaServicio extends ServicioBaseImpl<Entrega> {

  @Autowired
  private EntregaRepo entregaRepo;
  @Autowired
  private EstadoEntregaRepo estadoEntregaRepo;

  public EntregaServicio(RepoBase<Entrega> repoBase) {
    super(repoBase);
  }
}
