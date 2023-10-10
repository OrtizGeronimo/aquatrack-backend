package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.GuardarPedidoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.PedidoServicio;

@RestController
@RequestMapping(path = "/pedidos")
public class PedidoControlador{

    @Autowired
    private PedidoServicio pedidoServicio;

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(pedidoServicio.getAllPedidosExtraordinarios(page, size));
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_PEDIDOS')")
    public ResponseEntity<?> create(@RequestBody GuardarPedidoDTO pedido)
    {
        return ResponseEntity.ok().body(pedidoServicio.createPedido(pedido));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> detallar(@PathVariable("id") Long idPedido) throws RecordNotFoundException
    {
        return ResponseEntity.ok().body(pedidoServicio.detallarPedido(idPedido));
    }
}
