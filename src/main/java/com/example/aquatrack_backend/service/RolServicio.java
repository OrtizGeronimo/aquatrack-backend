package com.example.aquatrack_backend.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.aquatrack_backend.exception.UserWithOneRolePresentException;
import com.example.aquatrack_backend.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.dto.PermisoDTO;
import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.PermisoRol;
import com.example.aquatrack_backend.model.Rol;

@Service
public class RolServicio extends ServicioBaseImpl<Rol> {
    @Autowired
    private RolRepo rolRepo;

    @Autowired
    private PermisoRepo permisoRepo;

    @Autowired
    private RolUsuarioRepo rolUsuarioRepo;

    public RolServicio(RepoBase<Rol> repoBase) {
        super(repoBase);
    }

    public Page<RolDTO> findAll(int page, int size, String nombre, boolean mostrarInactivos) {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Pageable paging = PageRequest.of(page, size);
        return rolRepo.findAllByEmpresa(empresa.getId(), nombre, mostrarInactivos, paging).map(rol -> new ModelMapper().map(rol, RolDTO.class));
    }

    @Transactional
    public RolDTO createRol(GuardarRolDTO rol) {
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
        Rol rol = rolRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El rol solicitado no fue encontrado"));

        return rol.getPermisoRoles()
                .stream()
                .map(permisoRol -> new ModelMapper().map(permisoRol.getPermiso(), PermisoDTO.class))
                .sorted(Comparator.comparing(PermisoDTO::getId))
                .collect(Collectors.toList());
    }

    @Transactional
    public RolDTO update(Long id, GuardarRolDTO rol) throws RecordNotFoundException {
        Rol rolModificado = rolRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El rol solicitado no fue encontrado"));
        rolModificado.setNombre(rol.getNombre());

        List<Long> permisosActuales = rolModificado.getPermisoRoles().stream().map(permiso -> permiso.getPermiso().getId()).collect(Collectors.toList());
        List<Long> permisosRevocados = permisosActuales.stream().filter(permiso -> !rol.getIdPermisos().contains(permiso)).collect(Collectors.toList());
        List<Long> permisosNuevos = rol.getIdPermisos().stream().filter(permiso -> !permisosActuales.contains(permiso)).collect(Collectors.toList());
        rolModificado.getPermisoRoles().removeIf(permisoRol -> permisosRevocados.contains(permisoRol.getPermiso().getId()));
        for (Long permiso : permisosNuevos) {
            rolModificado.getPermisoRoles().add(new PermisoRol(permisoRepo.findById(permiso).get(), rolModificado));
        }
        rolRepo.save(rolModificado);
        return new ModelMapper().map(rolModificado, RolDTO.class);
    }

    @Transactional
    public void disable(Long id) throws Exception {
        Rol rolDeshabilitado = rolRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El rol solicitado no fue encontrado"));
        System.out.println("id:" + id + "cuentita: " + rolUsuarioRepo.usersWithRoleAsOnlyRole(rolDeshabilitado.getId()));
        if (rolUsuarioRepo.usersWithRoleAsOnlyRole(rolDeshabilitado.getId()) > 0){
            throw new UserWithOneRolePresentException("Hay al menos un usuario que tiene como único rol el que desea deshabilitar, no es posible eliminar el rol");
        }
        rolDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
        rolRepo.save(rolDeshabilitado);
    }

    @Transactional
    public RolDTO enable(Long id) throws RecordNotFoundException {
        Rol rolRehabilitado = rolRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El rol solicitado no fue encontrado"));
        rolRehabilitado.setFechaFinVigencia(null);
        rolRepo.save(rolRehabilitado);
        return new ModelMapper().map(rolRehabilitado, RolDTO.class);
    }
}
