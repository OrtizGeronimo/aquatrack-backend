package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Pago;
import com.example.aquatrack_backend.repo.PagoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class PagoServicio extends ServicioBaseImpl<Pago> {

  @Autowired
  private PagoRepo pagoRepo;

  public PagoServicio(RepoBase<Pago> repoBase) {
    super(repoBase);
  }
}
