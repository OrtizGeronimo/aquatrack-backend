package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.GuardarRutaDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.RutaServicio;

@RestController
@RequestMapping(path = "/rutas")
public class RutaControlador{

    @Autowired
    private RutaServicio rutaServicio;

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_RUTAS')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Long idDiaSemana,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String texto)
    {
        return ResponseEntity.ok().body(rutaServicio.findAll(page, size, idDiaSemana, texto, mostrar_inactivos));
    }

    @GetMapping("/nuevo")
    public ResponseEntity<?> rellenarForm(){
        return ResponseEntity.ok().body(rutaServicio.newRuta());
    }

    @PostMapping("")
    public ResponseEntity<?> crearRuta(@RequestBody GuardarRutaDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.crearRuta(dto));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LISTAR_RUTAS')")
    public ResponseEntity<?> detalle(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.detalleRuta(id));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody GuardarRutaDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.editarDiasRuta(id, dto));
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<?> editRuta(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.edit(id));
    }

    @GetMapping("/{id}/clientes")
    public ResponseEntity<?> clientes(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.clientes(id));
    }

    @PutMapping("/{id}/clientes")
    public ResponseEntity<?> asignarClientes(@PathVariable Long id, @RequestBody GuardarRutaDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(rutaServicio.asignarClientesRuta(id, dto));
    }

    @PutMapping("/{id}/editClientes")
    public ResponseEntity<?> editarClientes(@PathVariable Long id, @RequestBody GuardarRutaDTO dto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(rutaServicio.editarClientesRuta(id, dto));
    }

}
