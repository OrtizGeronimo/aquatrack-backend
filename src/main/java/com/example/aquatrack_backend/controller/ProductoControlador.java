package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Precio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.service.ProductoServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/productos")
public class ProductoControlador extends ControladorBaseImpl<Producto, ProductoServicioImpl>{

    @GetMapping("/{id}/precios")
    public ResponseEntity<?> getPrecios(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(servicio.getPrecios(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
}
