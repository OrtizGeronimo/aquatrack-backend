package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.repo.DomicilioRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class DomicilioServicio extends ServicioBaseImpl<Domicilio> {
  @Autowired
  private DomicilioRepo domicilioRepo;

  public DomicilioServicio(RepoBase<Domicilio> repoBase) {
    super(repoBase);
  }
}
