package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RolRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolServicioImpl extends ServicioBaseImpl<Rol> implements ServicioBase<Rol>{
    @Autowired
    private RolRepo rolRepo;
    public RolServicioImpl(RepoBase<Rol> repoBase) {
        super(repoBase);
    }

/*    @Transactional
    public RolDTO createRol(CrearRolDTO rol) {
        Rol rolNuevo = new Rol();
        rolNuevo.setNombre(rol.getNombre());
        rolNuevo.setPermisoRoles(rol.getIdPermisos()
                .stream()
                .map(permiso -> new PermisoRol(permisoRepo.findById(permiso).get(), rolNuevo))
                .collect(Collectors.toList()));
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        System.out.println(empresa.getNombre() + " EMPRESAA " + empresa.getNumTelefono());
        rolNuevo.setEmpresa(empresa);
        rolRepo.save(rolNuevo);
        return new ModelMapper().map(rolNuevo, RolDTO.class);
    }*/
}
