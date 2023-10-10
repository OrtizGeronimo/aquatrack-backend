package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.service.UbicaciónServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/ubi")
@CrossOrigin(origins = "*")
public class UbicacionControlador {

    @Autowired
    UbicaciónServicio ubicaciónServicio;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUbicaciónUsuario(@PathVariable Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(ubicaciónServicio.obtenerUbicacion(id));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> guardarUbicacion(@RequestBody UbicacionDTO ubicacion){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(ubicaciónServicio.guardarUbicacion(ubicacion));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
}
