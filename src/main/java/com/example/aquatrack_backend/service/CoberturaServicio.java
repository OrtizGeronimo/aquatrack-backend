package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.CoberturaDTO;
import com.example.aquatrack_backend.dto.EditCoberturaUbicacionDTO;
import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.EmpresaSinCoberturaException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.CoberturaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.UbicacionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoberturaServicio extends ServicioBaseImpl<Cobertura> {

    @Autowired
    private CoberturaRepo coberturaRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    private UbicacionHelper ubicacionHelper = new UbicacionHelper();

    @Autowired
    private UbicacionRepo ubicacionRepo;

    @Autowired
    public CoberturaServicio(RepoBase<Cobertura> repoBase) {
        super(repoBase);
    }

    @Transactional
    public List<EditCoberturaUbicacionDTO> ubicacionesCubiertas() throws Exception {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        List<EditCoberturaUbicacionDTO> ubicaciones = new ArrayList<>();
        ubicaciones.add(EditCoberturaUbicacionDTO.builder().esEmpresa(true).latitud(empresa.getUbicacion().getLatitud()).longitud(empresa.getUbicacion().getLongitud()).build());
        List<Cliente> clientes = clienteRepo.findClientesByEmpresa(empresa);
        if (!clientes.isEmpty()) {
            for (Cliente cliente : clientes) {
                EditCoberturaUbicacionDTO ubiCliente = EditCoberturaUbicacionDTO.builder().esEmpresa(false).latitud(cliente.getDomicilio().getUbicacion().getLatitud()).longitud(cliente.getDomicilio().getUbicacion().getLongitud()).build();
                ubicaciones.add(ubiCliente);
            }

        }
        return ubicaciones;
    }

    @Transactional
    public CoberturaDTO verCobertura() throws Exception {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Cobertura cobertura = coberturaRepo.findCoberturaByEmpresa(empresa).orElseThrow(() -> new EmpresaSinCoberturaException(""));
        List<UbicacionDTO> vertices = cobertura.getUbicaciones().stream().map(ubicacion -> UbicacionDTO.builder().latitud(ubicacion.getLatitud()).longitud(ubicacion.getLongitud()).build()).collect(Collectors.toList());
        return CoberturaDTO.builder().id(cobertura.getId()).nombreEmpresa(empresa.getNombre()).vertices(vertices).build();
    }

    @Transactional(rollbackOn = {ValidacionException.class})
    public CoberturaDTO guardarCobertura(List<UbicacionDTO> ubicaciones) throws ValidacionException {
        Cobertura cobertura;
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Optional coberturaExistente = coberturaRepo.findCoberturaByEmpresa(empresa);
        if (coberturaExistente.isPresent()) {
            cobertura = (Cobertura) coberturaExistente.get();
            cobertura.getUbicaciones().clear();
            cobertura.getUbicaciones().addAll(ubicaciones.stream().map(ubicacion -> new Ubicacion(ubicacion.getLatitud(), ubicacion.getLongitud(), cobertura)).collect(Collectors.toList()));
            UbicacionDTO ubiEmpresa = UbicacionDTO.builder().latitud(empresa.getUbicacion().getLatitud()).longitud(empresa.getUbicacion().getLongitud()).build();
            if (!ubicacionHelper.estaContenida(ubiEmpresa, cobertura)) {
                throw new ValidacionException("La cobertura debe contener la ubicación de la empresa.");
            }

            List<Cliente> clientes = clienteRepo.findClientesByEmpresa(empresa);
            if (!clientes.isEmpty()) {
                for (Cliente cliente : clientes) {
                    UbicacionDTO ubiCliente = UbicacionDTO.builder().latitud(cliente.getDomicilio().getUbicacion().getLatitud()).longitud(cliente.getDomicilio().getUbicacion().getLongitud()).build();
                    if (!ubicacionHelper.estaContenida(ubiCliente, cobertura)) {
                        throw new ValidacionException("La cobertura debe contener a los domicilios de todos los clientes de la empresa.");
                    }
                }
            }
        } else {
            cobertura = new Cobertura();
            cobertura.setUbicaciones(ubicaciones.stream().map(ubicacion -> new Ubicacion(ubicacion.getLatitud(), ubicacion.getLongitud(), cobertura)).collect(Collectors.toList()));
            cobertura.setEmpresa(empresa);
            UbicacionDTO ubiEmpresa = UbicacionDTO.builder().latitud(empresa.getUbicacion().getLatitud()).longitud(empresa.getUbicacion().getLongitud()).build();
            if (!ubicacionHelper.estaContenida(ubiEmpresa, cobertura)) {
                throw new ValidacionException("La cobertura debe contener la ubicación de la empresa.");
            }
        }
        coberturaRepo.save(cobertura);
        CoberturaDTO dtoCobertura = new CoberturaDTO();
        dtoCobertura.setId(cobertura.getId());
        dtoCobertura.setNombreEmpresa(empresa.getNombre());
        dtoCobertura.setVertices(ubicaciones);
        return dtoCobertura;
    }

    @Transactional
    public List<EmpresaDTO> conocerCobertura(double latitud, double longitud) throws Exception {
        try {
            UbicacionDTO ubicacionCliente = UbicacionDTO.builder().latitud(latitud).longitud(longitud).build();
            List<Cobertura> coberturas = coberturaRepo.findAll();
            List<EmpresaDTO> empresas = new ArrayList<>();
            UbicacionHelper ubicacionHelper = new UbicacionHelper();
            for (Cobertura cobertura : coberturas) {
                boolean estaContenida = ubicacionHelper.estaContenida(ubicacionCliente, cobertura);
                if (estaContenida) {
                    EmpresaDTO empresa = new EmpresaDTO();
                    Empresa empresaCober = cobertura.getEmpresa();
                    empresa.setId(empresaCober.getId());
                    empresa.setNombreEmpresa(empresaCober.getNombre());
                    empresa.setNumTelefono(empresaCober.getNumTelefono());
                    empresa.setUrlEmpresa(empresaCober.getUrl());
                    empresa.setMailEmpresa(empresaCober.getEmail());
                    empresa.setUbicacion(UbicacionDTO.builder().latitud(empresaCober.getUbicacion().getLatitud()).longitud(empresaCober.getUbicacion().getLongitud()).build());
                    empresa.setMailEmpresa(empresaCober.getEmail());
                    empresa.setUrlEmpresa(empresaCober.getUrl());
                    if (empresaCober.getUbicacion() != null) {
                        empresa.setUbicacion(UbicacionDTO.builder().latitud(empresaCober.getUbicacion().getLatitud()).longitud(empresaCober.getUbicacion().getLongitud()).build());
                    }
                    empresas.add(empresa);
                }
            }
            return empresas;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
