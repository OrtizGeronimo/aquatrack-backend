package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.RolServicio;

@RestController
@RequestMapping(path = "/roles")
public class RolControlador {
    @Autowired
    private RolServicio rolServicio;
    ValidationHelper validationHelper = new ValidationHelper();

    @GetMapping(value = "/paged")
    @PreAuthorize("hasAuthority('LISTAR_ROLES')")
    public ResponseEntity<?> findAllPaged(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok().body(rolServicio.findAllPaged(page, size, nombre, mostrar_inactivos));
    }

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_ROLES')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok().body(rolServicio.findAll(nombre, mostrar_inactivos));
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_ROLES')")
    public ResponseEntity<?> create(@RequestBody GuardarRolDTO rol) {
        if(validationHelper.hasValidationErrors(rol)){
            return ResponseEntity.badRequest().body(validationHelper.getValidationErrors(rol));
        }
        return ResponseEntity.ok().body(rolServicio.createRol(rol));
    }

    @GetMapping(value = "/{id}/permisos")
    @PreAuthorize("hasAuthority('LISTAR_ROLES')")
    public ResponseEntity<?> findAllPermissionsByRole(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rolServicio.findAllPermissionsByRole(id));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('EDITAR_ROLES')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GuardarRolDTO rol) throws RecordNotFoundException {
        if(validationHelper.hasValidationErrors(rol)){
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(rol));
        }
        return ResponseEntity.ok().body(rolServicio.update(id, rol));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_ROLES')")
    public ResponseEntity<?> disable(@PathVariable Long id) throws Exception {
        rolServicio.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/enable")
    @PreAuthorize("hasAuthority('EDITAR_ROLES')")
    public ResponseEntity<?> enable(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rolServicio.enable(id));
    }
}
