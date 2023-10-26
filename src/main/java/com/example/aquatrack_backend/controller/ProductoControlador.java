package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.helpers.ValidationHelper;
import com.example.aquatrack_backend.exception.ProductoNoValidoException;
import com.example.aquatrack_backend.service.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/productos")
public class ProductoControlador {

    @Autowired
    private ProductoServicio productoServicio;
    ValidationHelper validationHelper = new ValidationHelper();

    @GetMapping("/{id}/precios")
    public ResponseEntity<?> getPrecios(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productoServicio.getPrecios(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('LISTAR_PRODUCTOS')")
    public ResponseEntity<?> getProductosActivos(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "false") boolean mostrar_inactivos,
                                                 @RequestParam(required = false) String nombre,
                                                 @RequestParam(defaultValue = "0") int precio1,
                                                 @RequestParam(defaultValue = "20000") int precio2) throws Exception {
        return ResponseEntity.ok().body(productoServicio.getProductosActivos(page, size, nombre, mostrar_inactivos, precio1, precio2));
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImagen(@PathVariable Long id) throws IOException, RecordNotFoundException {
        // productoServicio.getImagen(id);
        return ResponseEntity.ok(productoServicio.getImagen(id));
    }

    @PostMapping("/uploadImage/{id}")
    public ResponseEntity<?> uploadImagen(@RequestParam("imagen") MultipartFile imagen, @PathVariable Long id) throws IOException, RecordNotFoundException {
        productoServicio.uploadImage(imagen, id);
        HashMap<String, String> map = new HashMap();
        map.put("message", "Image uploaded successfully");
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('CREAR_PRODUCTOS')")
    public ResponseEntity<?> create(@RequestBody GuardarProductoDTO producto) throws ProductoNoValidoException {
        if(validationHelper.hasValidationErrors(producto)){
            return ResponseEntity.badRequest().body(validationHelper.getValidationErrors(producto));
        }
        return ResponseEntity.ok().body(productoServicio.createProducto(producto));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('EDITAR_PRODUCTOS')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GuardarProductoDTO producto) throws RecordNotFoundException {
        return ResponseEntity.ok().body(productoServicio.updateProducto(id, producto));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ELIMINAR_PRODUCTOS')")
    public ResponseEntity<?> disable(@PathVariable Long id) throws Exception {
        productoServicio.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/enable")
    @PreAuthorize("hasAuthority('EDITAR_PRODUCTOS')")
    public ResponseEntity<?> enable(@PathVariable Long id) throws RecordNotFoundException {
        return ResponseEntity.ok().body(productoServicio.enable(id));
    }

    @GetMapping(value = "/{codigo}")
    public ResponseEntity<?> verificarCodigoExistente(@PathVariable String codigo) {
        return ResponseEntity.ok().body(productoServicio.verificarCodigoExistente(codigo));
    }

    /*    @PostMapping("/{id}/precios")
      public ResponseEntity<?> setPrecio(@PathVariable Long id, @RequestBody Precio precio){
          try {
              return ResponseEntity.status(HttpStatus.OK).body(productoServicio.setPrecio(id, precio));
          } catch (Exception e) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
          }
      }*/
    @GetMapping(value = "/listar-pedido")
    public ResponseEntity<?> listarProductosCrearPedido() {
        return ResponseEntity.ok().body(productoServicio.listarProductosCrearPedido());
    }

    @GetMapping(value = "/listar-pedido-mobile")
    public ResponseEntity<?> listarProductosCrearPedidoMobile() {
        return ResponseEntity.ok().body(productoServicio.listarProductosCrearPedidoMobile());
    }
}
