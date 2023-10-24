package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.PagoDataDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.PagoServicio;

@RestController
@RequestMapping(path = "/pagos")
public class PagoControlador{

    @Autowired
    private PagoServicio pagoServicio;

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('LISTAR_PAGOS')")
    public ResponseEntity<?> listarPagos(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pagoServicio.listarPagosPorCliente(id));
    }


    @PostMapping("/charge/{idCliente}")
//    @PreAuthorize("hasAuthority('EDITAR_PAGOS')")
    public ResponseEntity<?> cargarPagoCliente(@PathVariable Long idCliente, @RequestBody PagoDataDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pagoServicio.cargarPago(idCliente, dto.getMonto(), dto.getIdMedioPago()));
    }

    @PostMapping("/pay/{idEntrega}")
//    @PreAuthorize("hasAuthority('EDITAR_PAGOS')")
    public ResponseEntity<?> cobrar(@PathVariable Long idEntrega, @RequestBody PagoDataDTO dto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(pagoServicio.cobrar(idEntrega, dto.getMonto(), dto.getIdMedioPago()));
    }
}
