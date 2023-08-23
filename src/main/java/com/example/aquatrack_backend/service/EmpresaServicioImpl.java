package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.aquatrack_backend.helpers.UbicacionHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServicioImpl extends ServicioBaseImpl<Empresa> implements ServicioBase<Empresa>{
    @Autowired
    private EmpresaRepo empresaRepo;
    private CoberturaRepo coberturaRepo;
    private UbicacionHelper ubicacionHelper;

    public EmpresaServicioImpl(RepoBase<Empresa> repoBase) {
        super(repoBase);
    }

    public Empresa guardarCobertura(List<Ubicacion> ubicaciones, Long empresaId) throws Exception{
        try{
            Optional<Empresa> optEmpresa = empresaRepo.findById(empresaId);
            Empresa empresa = optEmpresa.get();
            Cobertura cobertura = new Cobertura();
            cobertura.setUbicaciones(ubicaciones);
            System.out.println("termino");
            empresa.setCobertura(cobertura);
            coberturaRepo.save(cobertura);
            System.out.println("termino");
            return empresa;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<Empresa> conocerCobertura(Ubicacion ubicacionCliente) throws Exception{
        List<Cobertura> coberturas = coberturaRepo.findAll();
        List<Empresa> empresas = new ArrayList<>();
        for (Cobertura cobertura :coberturas) {
            boolean estaContenida = ubicacionHelper.estaContenida(ubicacionCliente, cobertura);
            if(estaContenida){
                empresas.add(cobertura.getEmpresa());
            }
        }
        return empresas;
    }
}
