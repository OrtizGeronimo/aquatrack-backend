package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserUnauthorizedException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.service.ClienteServicio;
import com.example.aquatrack_backend.service.DeudaServicio;
import com.example.aquatrack_backend.service.PagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping(path = "/clientes")
public class ClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private DeudaServicio deudaServicio;
    private ValidationHelper validationHelper = new ValidationHelper<>();

    @Autowired
    private PagoServicio pagoServicio;

    @GetMapping(value = "")
    @PreAuthorize("hasAuthority('LISTAR_CLIENTES')")
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "false") boolean mostrar_inactivos, @RequestParam(required = false) String texto) {
        return ResponseEntity.ok().body(clienteServicio.findAll(page, size, texto, mostrar_inactivos));
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

    @PostMapping(value = "/dni")
    public ResponseEntity<?> validarDni(@RequestBody ValidarDniDTO validacion) throws RecordNotFoundException {
        return ResponseEntity.ok().body(clienteServicio.validarDni(validacion));
    }

    @PostMapping(value = "/app")
    public ResponseEntity<?> createFromApp(@RequestBody GuardarClienteDTO cliente) throws RecordNotFoundException {
        if (validationHelper.hasValidationErrors(cliente)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.createClientFromApp(cliente));
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_CLIENTES')")
    public ResponseEntity<?> createFromWeb(@RequestBody GuardarClienteWebDTO cliente) throws Exception {
        if (validationHelper.hasValidationErrors(cliente)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.createFromWeb(cliente));
    }

    @GetMapping(value = "/{id}/editar")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTES')")
    public ResponseEntity<?> editClient(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(clienteServicio.clienteForEdit(id));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('EDITAR_CLIENTES')")
    public ResponseEntity<?> updateClientWeb(@PathVariable Long id, @RequestBody GuardarClienteWebDTO cliente) throws RecordNotFoundException, EntidadNoValidaException {
        if (validationHelper.hasValidationErrors(cliente)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.updateFromWeb(id, cliente));
    }

    @GetMapping(value = "/entrega-pendiente")
    public ResponseEntity<?> getEntregaPendienteCliente() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getEntregaPendienteCliente());
    }

    @GetMapping(value = "/proxima-entrega")
    public ResponseEntity<?> getProximaEntregaCliente() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getProximaEntregaCliente());
    }

    @GetMapping(value = "/mobile-info")
    public ResponseEntity<?> infoCliente() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getPersonalInfo());
    }

    @PutMapping(value = "/mobile")
    public ResponseEntity<?> updateCurrentClientMobile(@RequestBody EditarClienteMobileDTO cliente) throws UserUnauthorizedException {
        if (validationHelper.hasValidationErrors(cliente)) {
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(cliente));
        }
        return ResponseEntity.ok().body(clienteServicio.editarClienteMobile(cliente));
    }

    @PutMapping(value = "/mobile-address")
    public ResponseEntity<?> updateCurrentClientAddressMobile(@RequestBody EditarDomicilioMobileDTO domicilio) throws UserUnauthorizedException, EntidadNoValidaException {
        return ResponseEntity.ok().body(clienteServicio.editarDomicilioMobile(domicilio));
    }

    @GetMapping("/pedido-habitual")
    public ResponseEntity<?> getPedidoHabitualMobile() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getPedidoHabitual());
    }

    @PostMapping("/pedido-habitual")
    public ResponseEntity<?> crearPedidoHabitualMobile(@RequestBody GuardarPedidoHabitualMobileDTO pedido) throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.crearPedidoHabitual(pedido));
    }

    @PutMapping("/pedido-habitual")
    public ResponseEntity<?> editarPedidoHabitualMobile(@RequestBody GuardarPedidoHabitualMobileDTO pedido) throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.updatePedidoHabitual(pedido));
    }

    @GetMapping("/edit-pedido-habitual")
    public ResponseEntity<?> editPedidoHabitualMobile() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.editPedidoHabitual());
    }

    @GetMapping("/{id}/deuda")
//    @PreAuthorize("hasAuthority('LISTAR_DEUDAS')")
    public ResponseEntity<?> detallarDeuda(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(deudaServicio.detalleDeuda(id));
    }


    @GetMapping("/{id}/pagos")
//    @PreAuthorize("hasAuthority('LISTAR_PAGOS')")
    public ResponseEntity<?> listarPagos(@PathVariable Long id, @RequestParam(required = false) Long idMedioPago, @RequestParam(required = false) Long idEmpleado, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCreacionDesde, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCreacionHasta, @RequestParam(required = false) BigDecimal montoDesde, @RequestParam(required = false) BigDecimal montoHasta) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pagoServicio.listarPagosPorCliente(id, idMedioPago, idEmpleado, fechaCreacionDesde, fechaCreacionHasta, montoDesde, montoHasta));
    }


    @PostMapping("/{id}/pagos")
    public ResponseEntity<?> cargarPagoCliente(@PathVariable Long id, @RequestBody PagoDataDTO dto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(pagoServicio.cargarPago(id, dto.getMonto(), dto.getIdMedioPago()));
    }

    @DeleteMapping("/{id}/pagos/{idPago}")
    public ResponseEntity<?> anularPago(@PathVariable Long id, @PathVariable Long idPago) throws RecordNotFoundException {
        pagoServicio.anularPago(idPago, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/ubicacion-repartidor")
    public ResponseEntity<?> getUbicacionRepartidor() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getUbicacionRepartidor());
    }

    @GetMapping(value = "/ubicacion-cliente")
    public ResponseEntity<?> getUbicacionCliente() throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getUbicacionCliente());
    }

    @GetMapping("/entregas-mobile")
    public ResponseEntity<?> getEntregasMobile(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVisitaDesde, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVisitaHasta, @RequestParam(defaultValue = "false") boolean sinPagar) throws UserUnauthorizedException {
        return ResponseEntity.ok().body(clienteServicio.getEntregasMobile(fechaVisitaDesde, fechaVisitaHasta, sinPagar));
    }

    @PutMapping("/cancelar-entrega-pendiente")
    public ResponseEntity<?> cancelarEntregaPendiente(@RequestBody CancelarEntregaClienteDTO observaciones) throws UserUnauthorizedException, ValidacionException {
        clienteServicio.cancelarEntregaPendiente(observaciones);
        return ResponseEntity.ok().build();
    }

}