package com.example.aquatrack_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Permiso;
import com.example.aquatrack_backend.repo.PermisoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class PermisoServicio extends ServicioBaseImpl<Permiso> {
  @Autowired
  private PermisoRepo permisoRepo;

  public PermisoServicio(RepoBase<Permiso> repoBase) {
    super(repoBase);
  }

  @Override
  public List<Permiso> findAll() {
    return permisoRepo.findAll();
  }
}
