package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Precio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aquatrack_backend.service.ProductoServicio;

@RestController
@RequestMapping(path = "/productos")
public class ProductoControlador{

    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping("/{id}/precios")
    public ResponseEntity<?> getPrecios(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productoServicio.getPrecios(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

/*    @PostMapping("/{id}/precios")
    public ResponseEntity<?> setPrecio(@PathVariable Long id, @RequestBody Precio precio){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productoServicio.setPrecio(id, precio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }*/
}
