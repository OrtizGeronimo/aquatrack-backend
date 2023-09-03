package com.example.aquatrack_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> getProductosActivos(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                     @RequestParam(required = false) String nombre) {
    return ResponseEntity.ok().body(productoServicio.getProductosActivos(page, size, nombre, mostrar_inactivos));
    }
}
