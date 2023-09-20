package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.CodigoDTO;
import com.example.aquatrack_backend.dto.GuardarClienteDTO;
import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.ClienteServicio;

@RestController
@RequestMapping(path = "/clientes")
public class ClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    private ValidationHelper validationHelper = new ValidationHelper<>();

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_CLIENTES')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok().body(clienteServicio.findAll(page, size, nombre, mostrar_inactivos));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTES')")
    public ResponseEntity<?> updateCliente(@RequestBody GuardarClienteDTO cliente, @PathVariable Long id) throws RecordNotFoundException {
        if (validationHelper.hasValidationErrors(cliente)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.updateCliente(cliente, id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_CLIENTES')")
    public ResponseEntity<?> disableCliente(@PathVariable Long id) throws Exception {
        clienteServicio.disableCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/enable")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTES')")
    public ResponseEntity<?> enableCliente(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(clienteServicio.enableCliente(id));
    }

    @PostMapping(value = "/codigo")
    @PreAuthorize("hasAuthority('CREAR_CLIENTES')")
    public ResponseEntity<?> altaEmpresa(@RequestBody CodigoDTO codigo) throws RecordNotFoundException {
        return ResponseEntity.ok().body(clienteServicio.altaEmpresa(codigo));
    }

    @PostMapping(value = "/{empresa_id}")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTES')")
    public ResponseEntity<?> createFromApp(@RequestBody GuardarClienteDTO cliente, @PathVariable Long empresa_id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(clienteServicio.createFromApp(cliente, empresa_id));
    }
}