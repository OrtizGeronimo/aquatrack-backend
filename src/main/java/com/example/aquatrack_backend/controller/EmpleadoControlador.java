package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.dto.GuardarEmpleadoDTO;
import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.EmpleadoServicio;

@RestController
@RequestMapping(path = "/empleados")
public class EmpleadoControlador{
    @Autowired
    private EmpleadoServicio empleadoServicio;
    ValidationHelper validationHelper = new ValidationHelper();

    @GetMapping(value= "")
    @PreAuthorize("hasAuthority('LISTAR_EMPLEADOS')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok().body(empleadoServicio.findAllByEnterprise(page, size, nombre, mostrar_inactivos));
    }

    @GetMapping(value = "/tipos")
    public ResponseEntity<?> findAllTiposActive(){
        return ResponseEntity.ok().body(empleadoServicio.findAllTiposActive());
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_EMPLEADOS')")
    public ResponseEntity<?> create(@RequestBody GuardarEmpleadoDTO empleado) throws RecordNotFoundException{
        if(validationHelper.hasValidationErrors(empleado)){
            return ResponseEntity.badRequest().body(validationHelper.getValidationErrors(empleado));
        }
        return ResponseEntity.ok().body(empleadoServicio.createEmpleado(empleado));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('LISTAR_EMPLEADOS')")
    public ResponseEntity<?> detail(@PathVariable("id") Long id) throws RecordNotFoundException {
        return ResponseEntity.ok(empleadoServicio.detail(id));
    }
}
