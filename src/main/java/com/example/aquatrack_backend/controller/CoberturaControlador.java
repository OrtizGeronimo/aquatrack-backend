package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dtos.DTOUbicacion;
import com.example.aquatrack_backend.service.CoberturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/coberturas")
@CrossOrigin(origins = "*")
public class CoberturaControlador {

    @Autowired
    CoberturaServicio coberturaServicio;

    @PostMapping("/{id}")
    public ResponseEntity<?> guardarCobertura(@PathVariable Long id, @RequestBody List<DTOUbicacion> ubicaciones){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.guardarCobertura(ubicaciones, id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestBody DTOUbicacion ubicacionCliente){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.conocerCobertura(ubicacionCliente));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
}
