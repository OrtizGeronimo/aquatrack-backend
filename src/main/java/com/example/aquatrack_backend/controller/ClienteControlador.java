package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.CodigoDTO;
import com.example.aquatrack_backend.dto.GuardarClienteDTO;
import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.dto.ValidarDniDTO;
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
                                     @RequestParam(required = false) String texto)
    {
        return ResponseEntity.ok().body(clienteServicio.findAll(page, size, texto, mostrar_inactivos));
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
    public ResponseEntity<?> altaEmpresa(@RequestBody CodigoDTO codigo) throws RecordNotFoundException {
        return ResponseEntity.ok().body(clienteServicio.altaEmpresa(codigo));
    }
    
    @PostMapping(value= "/dni")
    public ResponseEntity<?> validarDni(@RequestBody ValidarDniDTO validacion) throws RecordNotFoundException{
        return ResponseEntity.ok().body(clienteServicio.validarDni(validacion));
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createFromApp(@RequestBody GuardarClienteDTO cliente) throws RecordNotFoundException {
        if(validationHelper.hasValidationErrors(cliente)){
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.createClientFromApp(cliente));
    }
}