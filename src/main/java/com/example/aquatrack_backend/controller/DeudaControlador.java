package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.service.DeudaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/deudas")
public class DeudaControlador {

    @Autowired
    private DeudaServicio deudaServicio;

    @GetMapping("/mobile")
//    @PreAuthorize("hasAuthority('LISTAR_DEUDAS')")
    public ResponseEntity<?> detallarDeudaMobile() throws RecordNotFoundException {
        return ResponseEntity.ok().body(deudaServicio.detalleDeudaMobile());
    }

    @PostMapping("/{id}/recalculate")
//    @PreAuthorize("hasAuthority('EDITAR_DEUDAS')")
    public ResponseEntity<?> recalcular(@PathVariable Long id) throws RecordNotFoundException {
        deudaServicio.recalcularDeuda(id);
        return ResponseEntity.ok().build();
    }

}
