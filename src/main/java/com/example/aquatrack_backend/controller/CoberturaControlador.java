package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.service.CoberturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/coberturas")
public class CoberturaControlador {

    @Autowired
    CoberturaServicio coberturaServicio;

    @GetMapping("")
    @PreAuthorize("hasAuthority('DETALLAR_COBERTURAS')")
    public ResponseEntity<?> verCobertura() throws Exception{
        return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.verCobertura());
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('CREAR_COBERTURAS')")
    public ResponseEntity<?> crearCobertura(@RequestBody List<UbicacionDTO> vertices) {
        return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.guardarCobertura(vertices));
    }

    @GetMapping("/conocer_cercana")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestParam double latitud, @RequestParam double longitud) throws Exception{
      return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.conocerCobertura(latitud, longitud));
    }
}
