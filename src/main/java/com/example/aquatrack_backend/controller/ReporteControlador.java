package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.service.RepartoServicio;
import com.example.aquatrack_backend.service.ReporteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
public class ReporteControlador {

    @Autowired
    private RepartoServicio repartoServicio;

    @Autowired
    private ReporteServicio reporteServicio;

    @GetMapping("/reparto/{id}")
    public ResponseEntity<InputStreamResource> reporteReparto(@PathVariable Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(repartoServicio.reporte(id));
    }

    @GetMapping("")
    public ResponseEntity<?> generarReporte(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        if (fechaDesde == null) {
            fechaDesde = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaHasta == null) {
            fechaHasta = LocalDate.now();
        }

        return ResponseEntity.ok().body(reporteServicio.generarReporte(fechaDesde, fechaHasta));
    }
}
