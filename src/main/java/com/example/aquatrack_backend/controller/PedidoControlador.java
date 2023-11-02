package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.GuardarPedidoDTO;
import com.example.aquatrack_backend.exception.PedidoNoValidoException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.service.PedidoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoServicio pedidoServicio;
    private ValidationHelper validationHelper = new ValidationHelper<>();

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombreCliente,
                                     @RequestParam(required = false) Long estadoPedido,
                                     @RequestParam(required = false) Long tipoPedido,
                                     @RequestParam(required = false) String fechaCoordinadaEntregaDesde,
                                     @RequestParam(required = false) String fechaCoordinadaEntregaHasta) {
        return ResponseEntity.ok().body(pedidoServicio.getAllPedidos(page, size, mostrar_inactivos, nombreCliente, estadoPedido, tipoPedido, fechaCoordinadaEntregaDesde, fechaCoordinadaEntregaHasta));
    }

    @GetMapping("/parametro")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> getParametrosBusqueda() {
        return ResponseEntity.ok().body(pedidoServicio.getParametrosBusqueda());
    }

    @GetMapping("/parametros")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> getParametrosPedidosWeb() {
        return ResponseEntity.ok().body(pedidoServicio.getParametrosPedidoWeb());
    }

    @GetMapping("/parametros/mobile")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> getParametrosPedidosMobile() {
        return ResponseEntity.ok().body(pedidoServicio.getParametrosPedidoMobile());
    }

    @GetMapping(value = "/domicilios")
    @PreAuthorize("hasAuthority('CREAR_PEDIDOS')")
    public ResponseEntity<?> getDomicilios() {
        return ResponseEntity.ok().body(pedidoServicio.getDomicilios());
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_PEDIDOS')")
    public ResponseEntity<?> createExtraordinarioWeb(@RequestBody GuardarPedidoDTO pedido) throws PedidoNoValidoException {
        if (validationHelper.hasValidationErrors(pedido)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(pedido));
        }
        return ResponseEntity.ok().body(pedidoServicio.createPedidoExtraordinarioWeb(pedido));
    }

    @PostMapping(value = "/mobile")
    @PreAuthorize("hasAuthority('CREAR_PEDIDOS')")
    public ResponseEntity<?> createExtraordinarioMobile(@RequestBody GuardarPedidoDTO pedido) throws PedidoNoValidoException {
        if (validationHelper.hasValidationErrors(pedido)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(pedido));
        }
        return ResponseEntity.ok().body(pedidoServicio.createPedidoExtraordinarioMobile(pedido));
    }

/*    @PostMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('CREAR_PEDIDOS')")
    public ResponseEntity<?> createPedidoAnticipado(@RequestBody GuardarPedidoAnticipadoDTO pedido, @PathVariable("id") Long idDomicilio) throws RecordNotFoundException
    {
        return ResponseEntity.ok().body(pedidoServicio.createPedidoAnticipado(pedido, idDomicilio));
    }*/

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('LISTAR_PEDIDOS')")
    public ResponseEntity<?> detallar(@PathVariable("id") Long idPedido) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pedidoServicio.detallarPedido(idPedido));
    }

    @PutMapping(value = "/{id}/aprobar")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDOS')")
    public ResponseEntity<?> aprobarPedido(/*@RequestBody AprobarPedidoDTO pedido,*/ @PathVariable("id") Long idPedido) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pedidoServicio.aprobarPedido(/*pedido,*/ idPedido));
    }

    @PutMapping(value = "/{id}/rechazar")
    @PreAuthorize("hasAuthority('EDITAR_PEDIDOS')")
    public ResponseEntity<?> rechazarPedido(@PathVariable("id") Long idPedido) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pedidoServicio.rechazarPedido(idPedido));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_PEDIDOS')")
    public ResponseEntity<?> cancelarPedido(@PathVariable("id") Long idPedido) throws RecordNotFoundException {
        pedidoServicio.cancelarPedido(idPedido);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/productos")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> listarProductos(@PathVariable("id") Long idPedido) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pedidoServicio.listarProductos(idPedido));
    }
}
