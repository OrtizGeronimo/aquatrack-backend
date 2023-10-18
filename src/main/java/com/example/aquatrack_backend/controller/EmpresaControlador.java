package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.EditarEmpresaDTO;
import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.service.CodigoTemporalServicio;
import com.example.aquatrack_backend.service.EmpresaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/empresas")
@CrossOrigin(origins = "*")
public class EmpresaControlador{

    @Autowired
    EmpresaServicio empresaServicio;
    @Autowired
    CodigoTemporalServicio codigoTemporalServicio;

    @GetMapping(path = "/obtener_codigo")
    @PreAuthorize("hasAuthority('CREAR_CLIENTES')")
    public ResponseEntity<?> obtenerCodigoAlta(){
        return ResponseEntity.ok().body(codigoTemporalServicio.generarCodigoAlta());
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('DETALLAR_EMPRESAS')")
    public ResponseEntity<?> detalleEmpresa() throws RecordNotFoundException {
        return ResponseEntity.ok().body(empresaServicio.detalleEmpresa());
    }

    @PutMapping("")
    @PreAuthorize("hasAuthority('EDITAR_EMPRESAS')")
    public ResponseEntity<?> editarEmpresa(@RequestBody EmpresaDTO dto) throws EntidadNoValidaException {
        return ResponseEntity.ok().body(empresaServicio.editarEmpresa(dto));
    }
}