package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.dto.PermisoDTO;
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

  public List<PermisoDTO> findAll() {
    return permisoRepo.findAll()
        .stream()
        .map(permiso -> new ModelMapper().map(permiso, PermisoDTO.class))
        .collect(Collectors.toList());
  }
}
