package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class EmpresaServicio extends ServicioBaseImpl<Empresa> {
  @Autowired
  private EmpresaRepo empresaRepo;

  public EmpresaServicio(RepoBase<Empresa> repoBase) {
    super(repoBase);
  }
}
