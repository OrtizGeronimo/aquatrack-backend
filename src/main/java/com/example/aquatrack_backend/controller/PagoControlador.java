package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.PagoDataDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.service.PagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/pagos")
public class PagoControlador {

    @Autowired
    private PagoServicio pagoServicio;

    @PostMapping("/pay/{idEntrega}")
//    @PreAuthorize("hasAuthority('EDITAR_PAGOS')")
    public ResponseEntity<?> cobrar(@PathVariable Long idEntrega, @RequestBody PagoDataDTO dto) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().body(pagoServicio.cobrar(idEntrega, dto.getMonto(), dto.getIdMedioPago()));
    }

    @GetMapping("/parametros")
    public ResponseEntity<?> getPagoParametros() {
        return ResponseEntity.ok().body(pagoServicio.getPagoParametros());
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<?> getAllMediosPago() {
        return ResponseEntity.ok().body(pagoServicio.getAllMediosPago());
    }
}
