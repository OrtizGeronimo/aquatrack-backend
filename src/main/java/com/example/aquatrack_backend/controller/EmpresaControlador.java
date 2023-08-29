package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.service.EmpresaServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/empresas")
@CrossOrigin(origins = "*")
public class EmpresaControlador extends ControladorBaseImpl<Empresa, EmpresaServicioImpl> {

    @Autowired
    EmpresaServicioImpl empresaServicio;

    @PostMapping("/cobertura/{id}")
    public ResponseEntity<?> guardarCobertura(@PathVariable Long id, @RequestBody List<UbicacionDTO> ubicaciones){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(empresaServicio.guardarCobertura(ubicaciones, id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

    @PostMapping("/cobertura/cliente")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestBody UbicacionDTO ubicacionCliente){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(empresaServicio.conocerCobertura(ubicacionCliente));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
}