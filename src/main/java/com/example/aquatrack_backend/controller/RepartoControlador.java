package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.AsignarRepartidorDTO;
import com.example.aquatrack_backend.dto.FinalizarRepartoIncompletoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.service.RepartoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador{

    @Autowired
    private RepartoServicio servicio;

    private ValidationHelper validationHelper = new ValidationHelper();


//    @PostMapping("/{id}")
//    @PreAuthorize("hasAuthority('CREAR_REPARTOS')")
//    public ResponseEntity<?> crearReparto(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
//            return ResponseEntity.ok().body(servicio.crearReparto(id));
//    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('CREAR_REPARTOS')")
    public ResponseEntity<?> crearRepartoManual(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.crearRepartoManual(id));
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('LISTAR_REPARTOS')")
    public ResponseEntity<?> listarRepartos(@RequestParam(required = false) String nombreRuta,
                                            @RequestParam(required = false) Integer cantidadEntregasDesde,
                                            @RequestParam(required = false) Integer cantidadEntregasHasta,
                                            @RequestParam(required = false) Integer estado,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) throws RecordNotFoundException {
        return ResponseEntity.ok().body(servicio.listarRepartos(nombreRuta, cantidadEntregasDesde, cantidadEntregasHasta, estado, page, size));
    }

    @PutMapping("/desginarHorario")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> desginarHorarioGeneracionAutomatica(@RequestParam Integer hora, @RequestParam Integer minuto) throws ValidacionException {
        servicio.designarHoraGeneracionAutomaticaReparto(hora, minuto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/asignarRepartidor")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> asignarRepartidor(@RequestBody AsignarRepartidorDTO dto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.asignarRepartidor(dto.getIdReparto(), dto.getIdRepartidor()));
    }

    @PutMapping("/iniciarReparto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> iniciarReparto(@RequestParam Long idReparto) throws RecordNotFoundException {
        servicio.iniciarReparto(idReparto);
        return ResponseEntity.ok().body("El reparto inició su ejecución");
    }

    @PutMapping("/cancelarReparto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> cancelarReparto(@RequestParam Long idReparto) throws RecordNotFoundException, ValidacionException {
        servicio.cancelarReparto(idReparto);
        return ResponseEntity.ok().body("El reparto fue cancelado");
    }

    @PutMapping("/finalizarRepartoIncompleto")
    @PreAuthorize("hasAuthority('EDITAR_REPARTOS')")
    public ResponseEntity<?> finalizarRepartoIncompleto(@RequestBody FinalizarRepartoIncompletoDTO dto) throws RecordNotFoundException, ValidacionException {
        if(validationHelper.hasValidationErrors(dto)){
            return ResponseEntity.unprocessableEntity().body(validationHelper.getValidationErrors(dto));
        }
        servicio.finalizarRepartoIncompleto(dto.getIdReparto(), dto.getObservaciones());
        return ResponseEntity.ok().body("El reparto fue finalizado como incompleto correctamente");
    }

}
