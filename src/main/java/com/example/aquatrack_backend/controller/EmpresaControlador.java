package com.example.aquatrack_backend.controller;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Ubicacion;
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
    public ResponseEntity<?> guardarCobertura(@PathVariable Long id, @RequestBody List<Ubicacion> ubicacions){
        try{
            System.out.println("entro al controller");
            return ResponseEntity.status(HttpStatus.OK).body(empresaServicio.guardarCobertura(ubicacions, id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }

    @GetMapping("/cobertura/cliente")
    public ResponseEntity<?> conocerCoberturaCercana(@RequestBody Ubicacion ubicacionCliente){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(empresaServicio.conocerCobertura(ubicacionCliente));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error. Por favor intente mas tarde.\"}");
        }
    }
}
