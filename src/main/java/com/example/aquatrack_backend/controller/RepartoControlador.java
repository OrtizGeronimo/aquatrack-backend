package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.FinalizarRepartoIncompletoDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserUnauthorizedException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.service.EntregaServicio;
import com.example.aquatrack_backend.service.RepartoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador {

    @Autowired
    private RepartoServicio servicio;

    @Autowired
    private EntregaServicio entregaServicio;

    private ValidationHelper validationHelper = new ValidationHelper();


    //    @PostMapping("/{id}")
//    @PreAuthorize("hasAuthority('CREAR_REPARTOS')")
//    public ResponseEntity<?> crearReparto(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
//            return ResponseEntity.ok().body(servicio.crearReparto(id));
//    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LISTAR_REPARTOS')")
    public ResponseEntity<?> detallarReparto(@PathVariable("id") Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(servicio.detalleReparto(id));
    }

    @GetMapping("/{id}/entregas")
    @PreAuthorize("hasAuthority('LISTAR_ENTREGAS')")
    public ResponseEntity<?> listarEntregasReparto(@PathVariable("id") Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(entregaServicio.findAllEntregasByReparto(id));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('CREAR_REPARTOS')")
    public ResponseEntity<?> crearRepartoManual(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.crearRepartoManual(id));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('LISTAR_REPARTOS')")
    public ResponseEntity<?> listarRepartos(@RequestParam(required = false) Long ruta,
                                            @RequestParam(required = false) Long repartidor,
                                            @RequestParam(required = false) Long estado,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) throws RecordNotFoundException {
        return ResponseEntity.ok().body(servicio.listarRepartos(estado, repartidor, ruta, page, size));
    }

    @PutMapping("/desginarHorario")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> desginarHorarioGeneracionAutomatica(@RequestParam Integer hora, @RequestParam Integer minuto) throws ValidacionException {
        servicio.designarHoraGeneracionAutomaticaReparto(hora, minuto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/asignarRepartidor")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> getAsignarRepartidores() {
        return ResponseEntity.ok().body(servicio.getAsignarRepartidor());
    }

    @PutMapping("/{id}/asignarRepartidor/{repartidor_id}")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> asignarRepartidor(@PathVariable("id") Long id, @PathVariable("repartidor_id") Long repartidorId) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.asignarRepartidor(id, repartidorId));
    }

    @PutMapping("/{id}/iniciarReparto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> iniciarReparto(@PathVariable Long id) throws RecordNotFoundException, ValidacionException, UserUnauthorizedException {
        return ResponseEntity.ok().body(servicio.iniciarReparto(id));
    }

    @PutMapping("/{id}/cancelarReparto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> cancelarReparto(@PathVariable("id") Long idReparto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.cancelarReparto(idReparto));
    }

    @GetMapping("/{id}/entregasPendientes")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> entregasPendientes(@PathVariable("id") Long idReparto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.checkEntregasIncompletas(idReparto));
    }


    @PutMapping("/{id}/finalizarReparto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> finalizarRepartoIncompleto(@PathVariable("id") Long idReparto, @RequestBody FinalizarRepartoIncompletoDTO dto) throws RecordNotFoundException, EntidadNoValidaException {
        return ResponseEntity.ok().body(servicio.finalizarReparto(idReparto, dto.getObservaciones()));
    }

    @GetMapping("/parametros")
    @PreAuthorize("hasAuthority('LISTAR_REPARTOS')")
    public ResponseEntity<?> getParametrosRepartos() {
        return ResponseEntity.ok().body(servicio.getParametrosReparto());
    }

    @GetMapping("/asignados")
    public ResponseEntity<?> getRepartosAsignados(@RequestParam Long estado) throws UserUnauthorizedException {
        return ResponseEntity.ok().body(servicio.getRepartosAsignados(estado));
    }
}
