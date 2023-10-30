package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.service.PagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/pagos")
public class PagoControlador {

    @Autowired
    private PagoServicio pagoServicio;

    @GetMapping("/parametros")
    public ResponseEntity<?> getPagoParametros() {
        return ResponseEntity.ok().body(pagoServicio.getPagoParametros());
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<?> getAllMediosPago() {
        return ResponseEntity.ok().body(pagoServicio.getAllMediosPago());
    }
}
