package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dtos.DTOEmpresa;
import com.example.aquatrack_backend.dtos.DTOUbicacion;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.UbicacionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.aquatrack_backend.helpers.UbicacionHelper;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaServicioImpl extends ServicioBaseImpl<Empresa> implements ServicioBase<Empresa>{
    @Autowired
    private EmpresaRepo empresaRepo;
    @Autowired
    private CoberturaRepo coberturaRepo;
    @Autowired
    private UbicacionRepo ubicacionRepo;

    public EmpresaServicioImpl(RepoBase<Empresa> repoBase) {
        super(repoBase);
    }

    @Transactional
    public Cobertura guardarCobertura(List<DTOUbicacion> ubicaciones, Long empresaId) throws Exception{
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
            return cobertura;
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
