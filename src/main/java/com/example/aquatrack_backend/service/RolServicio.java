package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.dto.CrearRolDTO;
import com.example.aquatrack_backend.dto.PermisoDTO;
import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.PermisoRol;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.PermisoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RolRepo;

@Service
public class RolServicio extends ServicioBaseImpl<Rol> {
  @Autowired
  private RolRepo rolRepo;

  @Autowired
  private PermisoRepo permisoRepo;

  public RolServicio(RepoBase<Rol> repoBase) {
    super(repoBase);
  }

  public List<RolDTO> findAll() {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    return empresa.getRoles()
        .stream()
        .map(rol -> new ModelMapper().map(rol, RolDTO.class))
        .collect(Collectors.toList());
  }

  @Transactional
  public RolDTO createRol(CrearRolDTO rol) {
    Rol rolNuevo = new Rol();
    rolNuevo.setNombre(rol.getNombre());
    rolNuevo.setPermisoRoles(rol.getIdPermisos()
        .stream()
        .map(permiso -> new PermisoRol(permisoRepo.findById(permiso).get(), rolNuevo))
        .collect(Collectors.toList()));
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    rolNuevo.setEmpresa(empresa);
    rolRepo.save(rolNuevo);
    return new ModelMapper().map(rolNuevo, RolDTO.class);
  }

  @Transactional
  public List<PermisoDTO> findAllPermissionsByRole(Long id) throws RecordNotFoundException {
    Rol rol = rolRepo.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("El rol solicitado no fue encontrado"));

    return rol.getPermisoRoles()
        .stream()
        .map(permisoRol -> new ModelMapper().map(permisoRol.getPermiso(), PermisoDTO.class))
        .collect(Collectors.toList());
  }
}
