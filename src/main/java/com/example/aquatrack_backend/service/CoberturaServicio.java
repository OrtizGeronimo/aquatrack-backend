package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dtos.DTOCobertura;
import com.example.aquatrack_backend.dtos.DTOEmpresa;
import com.example.aquatrack_backend.dtos.DTOUbicacion;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoberturaServicio {

    @Autowired
    private CoberturaRepo coberturaRepo;
    @Autowired
    private EmpresaRepo empresaRepo;

    @Transactional
    public DTOCobertura guardarCobertura(List<DTOUbicacion> ubicaciones, Long empresaId) throws Exception{
        try{
            Cobertura cobertura = new Cobertura();
            cobertura.setUbicaciones(ubicaciones
                    .stream()
                    .map(ubicacion -> new Ubicacion(ubicacion.getLatitud(), ubicacion.getLongitud(), cobertura))
                    .collect(Collectors.toList()));
            Optional<Empresa> optEmpresa = empresaRepo.findById(empresaId);
            Empresa empresa = optEmpresa.get();
            cobertura.setEmpresa(empresa);
            coberturaRepo.save(cobertura);
            DTOCobertura dtoCobertura = new DTOCobertura();
            dtoCobertura.setId(cobertura.getId());
            dtoCobertura.setNombreEmpresa(empresa.getNombre());
            dtoCobertura.setUbicacions(ubicaciones);
            return dtoCobertura;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public List<DTOEmpresa> conocerCobertura(DTOUbicacion ubicacionCliente) throws Exception{
        try {
            List<Cobertura> coberturas = coberturaRepo.findAll();
            List<DTOEmpresa> empresas = new ArrayList<>();
            UbicacionHelper ubicacionHelper = new UbicacionHelper();
            for (Cobertura cobertura :coberturas) {
                boolean estaContenida = ubicacionHelper.estaContenida(ubicacionCliente, cobertura);
                if(estaContenida){
                    DTOEmpresa empresa = new DTOEmpresa();
                    Empresa empresaCober = cobertura.getEmpresa();
                    empresa.setNombre(empresaCober.getNombre());
                    empresa.setNumTelefono(empresaCober.getNumTelefono());
                    empresa.setUbicacion(empresaCober.getUbicacion());
                    empresas.add(empresa);
                }
            }
            return empresas;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
