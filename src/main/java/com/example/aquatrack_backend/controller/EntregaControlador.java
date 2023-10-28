package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.EntregaAusenteDTO;
import com.example.aquatrack_backend.dto.ProcesarEntregaDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.service.EntregaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/entregas")
public class EntregaControlador {

    @Autowired
    private EntregaServicio entregaServicio;

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_ENTREGAS')")
    public ResponseEntity<?> disableEntrega(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.disableEntrega(id));
    }

    @GetMapping(value = "/{id}/productos-a-entregar")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> productosAEntregar(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.productosEntregar(id));
    }

    @GetMapping(value = "/{id}/productos-domicilio")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> productosDomicilio(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.productosDomicilio(id));
    }

    @GetMapping(value = "/{id}/productos-entregados")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> productosEntregados(@PathVariable Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(entregaServicio.productosEntregados(id));
    }

    @PutMapping(value = "/{id}/ausente")
    @PreAuthorize("hasAuthority('EDITAR_ENTREGAS')")
    public ResponseEntity<?> entregaAusente(@PathVariable Long id, @RequestBody EntregaAusenteDTO observaciones) throws RecordNotFoundException, ValidacionException {
        entregaServicio.entregaAusente(id, observaciones);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/pedidos")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> pedidosEntregar(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.getEntregaPedidos(id));
    }

    @GetMapping(value = "/{id}/productos-devolver")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> productosDevolver(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.getProductosDevolver(id));
    }

    @PutMapping(value = "/{id}/procesar")
    @PreAuthorize("hasAuthority('EDITAR_ENTREGAS')")
    public ResponseEntity<?> procesarEntrega(@PathVariable Long id, @RequestBody ProcesarEntregaDTO entrega) throws RecordNotFoundException, ValidacionException, EntidadNoValidaException {
        entregaServicio.procesarEntrega(id, entrega);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/detalle-mobile")
    @PreAuthorize("hasAuthority('DETALLAR_ENTREGAS')")
    public ResponseEntity<?> detallarEntregaMobile(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.detallarEntregaMobile(id));
    }

    @GetMapping(value = "/{id}/detalle-web")
    @PreAuthorize("hasAuthority('DETALLAR_ENTREGAS')")
    public ResponseEntity<?> detallarEntregaWeb(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.detallarEntregaWeb(id));
    }
}