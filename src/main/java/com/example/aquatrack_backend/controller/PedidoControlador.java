package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.AprobarPedidoDTO;
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
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombreCliente,
                                     @RequestParam(defaultValue = "1") Long estadoPedido,
                                     @RequestParam(defaultValue = "2") Long tipoPedido,
                                     @RequestParam(required = false) String fechaCoordinadaEntregaDesde,
                                     @RequestParam(required = false) String fechaCoordinadaEntregaHasta)
    {
        return ResponseEntity.ok().body(pedidoServicio.getAllPedidos(page, size, mostrar_inactivos, nombreCliente, estadoPedido, tipoPedido, fechaCoordinadaEntregaDesde, fechaCoordinadaEntregaHasta));
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

    @PutMapping(value = "/{id}/aprobar")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDOS')")
    public ResponseEntity<?> aprobarPedido(@RequestBody AprobarPedidoDTO pedido, @PathVariable("id") Long idPedido) throws RecordNotFoundException
    {
        return ResponseEntity.ok().body(pedidoServicio.aprobarPedido(pedido, idPedido));
    }

    @PutMapping(value = "/{id}/rechazar")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDOS')")
    public ResponseEntity<?> rechazarPedido(@PathVariable Long idPedido) throws RecordNotFoundException
    {
        return ResponseEntity.ok().body(pedidoServicio.rechazarPedido(idPedido));
    }
}
