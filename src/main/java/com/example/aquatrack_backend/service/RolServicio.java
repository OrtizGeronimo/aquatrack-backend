package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RolRepo;

@Service
public class RolServicio extends ServicioBaseImpl<Rol> {
  @Autowired
  private RolRepo rolRepo;

  public RolServicio(RepoBase<Rol> repoBase) {
    super(repoBase);
  }

  @Override
  public List<Rol> findAll() {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    return empresa.getRoles();
  }
}
