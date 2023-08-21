package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.RolRepo;

@Service
public class RolServicio extends ServicioBase {
  @Autowired
  private RolRepo rolRepo;

  public List<RolDTO> findAll() {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    return empresa.getRoles()
        .stream()
        .map((Rol rol) -> {
          return RolDTO.builder()
              .id(rol.getId())
              .nombre(rol.getNombre())
              .fechaCreacion(rol.getFechaCreacion())
              .fechaFinVigencia(rol.getFechaFinVigencia())
              .build();
        })
        .collect(Collectors.toList());
  }
}
