package com.example.aquatrack_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class ClienteServicio extends ServicioBaseImpl<Cliente> {

  @Autowired
  private ClienteRepo clienteRepo;

  public ClienteServicio(RepoBase<Cliente> repoBase) {
    super(repoBase);
  }
}
