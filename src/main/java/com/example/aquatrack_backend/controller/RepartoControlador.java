package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.AsignarRepartidorDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import org.apache.coyote.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.RepartoServicio;

@RestController
@RequestMapping(path = "/repartos")
public class RepartoControlador{

    @Autowired
    private RepartoServicio servicio;


    @GetMapping("/{id}/crear")
    public ResponseEntity<?> crearReparto(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
            return ResponseEntity.ok().body(servicio.crearReparto(id));
    }

    @GetMapping("/{id}/crearManual")
    public ResponseEntity<?> crearRepartoManual(@PathVariable("id") Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.crearRepartoManual(id));
    }

    @GetMapping("")
    public ResponseEntity<?> listarRepartos(@RequestParam(required = false) String nombreRuta,
                                            @RequestParam(required = false) Integer cantidadEntregasDesde,
                                            @RequestParam(required = false) Integer cantidadEntregasHasta,
                                            @RequestParam(required = false) Integer estado,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) throws RecordNotFoundException {
        return ResponseEntity.ok().body(servicio.listarRepartos(nombreRuta, cantidadEntregasDesde, cantidadEntregasHasta, estado, page, size));
    }

    @GetMapping("/desginarHorario")
    public ResponseEntity<?> desginarHorarioGeneracionAutomatica(@RequestParam Integer hora, @RequestParam Integer minuto) throws ValidacionException {
        servicio.designarHoraGeneracionAutomaticaReparto(hora, minuto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/asignarRepartidor")
    public ResponseEntity<?> asignarRepartidor(@RequestBody AsignarRepartidorDTO dto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(servicio.asignarRepartidor(dto.getIdReparto(), dto.getIdRepartidor()));
    }

    @GetMapping("/iniciarReparto")
    public ResponseEntity<?> iniciarReparto(@RequestParam Long idReparto) throws RecordNotFoundException {
        servicio.iniciarReparto(idReparto);
        return ResponseEntity.ok().body("El reparto inició su ejecución");
    }



}
