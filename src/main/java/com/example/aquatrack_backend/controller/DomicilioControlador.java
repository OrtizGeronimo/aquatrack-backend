package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.DomicilioDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.ClienteNoCubiertoApp;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.DomicilioServicio;

@RestController
@RequestMapping(path = "/domicilios")
public class DomicilioControlador{
    @Autowired
    private DomicilioServicio domicilioServicio;

    @PostMapping("/ubicacion")
    public ResponseEntity<?> guardarUbicacionDomilio(@RequestBody UbicacionDTO ubicacionDTO) throws RecordNotFoundException, ClienteNoCubiertoApp {
        return ResponseEntity.ok().body(domicilioServicio.crearDomicilioUbicacion(ubicacionDTO));
    }
    @GetMapping("/ubicacion/{id}")
    public ResponseEntity<?> obtenerUbiDomicilio(@PathVariable Long idDomicilio){
        return ResponseEntity.ok().body(domicilioServicio.getDomicilioUbicacion(idDomicilio));
    }
}
