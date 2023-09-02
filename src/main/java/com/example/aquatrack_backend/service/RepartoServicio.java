package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Reparto;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class RepartoServicio extends ServicioBaseImpl<Reparto> {

  @Autowired
  private RepartoRepo repartoRepo;

  public RepartoServicio(RepoBase<Reparto> repoBase) {
    super(repoBase);
  }
}
