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

    @GetMapping("/conocer_cercana")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestParam double latitud, @RequestParam double longitud){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(coberturaServicio.conocerCobertura(latitud, longitud));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
 */

}
