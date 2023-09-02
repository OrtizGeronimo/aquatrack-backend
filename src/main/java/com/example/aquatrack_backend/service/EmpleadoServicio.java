package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class EmpleadoServicio extends ServicioBaseImpl<Empleado> {
  @Autowired
  private EmpleadoRepo empleadoRepo;

  public EmpleadoServicio(RepoBase<Empleado> repoBase) {
    super(repoBase);
  }
}
