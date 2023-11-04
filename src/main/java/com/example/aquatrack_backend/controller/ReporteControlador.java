package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.service.RepartoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class ReporteControlador {

    @Autowired
    private RepartoServicio repartoServicio;

    @GetMapping("/reparto/{id}")
    public ResponseEntity<InputStreamResource> reporteReparto(@PathVariable Long id) throws RecordNotFoundException, ValidacionException {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(repartoServicio.reporte(id));
    }
}
