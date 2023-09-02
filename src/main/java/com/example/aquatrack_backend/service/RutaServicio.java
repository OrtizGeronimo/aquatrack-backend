package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Ruta;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RutaRepo;

@Service
public class RutaServicio extends ServicioBaseImpl<Ruta> {

  @Autowired
  private RutaRepo rutaRepo;

  public RutaServicio(RepoBase<Ruta> repoBase) {
    super(repoBase);
  }
}
