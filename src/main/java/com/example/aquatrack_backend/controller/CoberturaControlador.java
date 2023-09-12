package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.service.CoberturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/coberturas")
public class CoberturaControlador {

    @Autowired
    CoberturaServicio coberturaServicio;

    @GetMapping("")
    public ResponseEntity<?> verCobertura() throws Exception{
        return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.verCobertura());
    }

    @PostMapping("")
    public ResponseEntity<?> crearCobertura(@RequestBody List<UbicacionDTO> vertices) {
        return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.guardarCobertura(vertices));
    }

/*
    @PostMapping("")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestBody UbicacionDTO ubicacionCliente){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.conocerCobertura(ubicacionCliente));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
 */

}
