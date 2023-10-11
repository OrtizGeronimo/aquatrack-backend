package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaServicio extends ServicioBaseImpl<Empresa> {
  @Autowired
  private EmpresaRepo empresaRepo;

  public EmpresaServicio(RepoBase<Empresa> repoBase) {
    super(repoBase);
  }

  public EmpresaDTO detalleEmpresa() throws RecordNotFoundException {

    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    EmpresaDTO response = new EmpresaDTO();

    response.setId(empresa.getId());
    response.setNombreEmpresa(empresa.getNombre());
    response.setUrlEmpresa(empresa.getUrl());
    response.setMailEmpresa(empresa.getEmail());
    response.setNumTelefono(empresa.getNumTelefono());
    response.setLatitud(empresa.getUbicacion().getLatitud());
    response.setLongitud(empresa.getUbicacion().getLongitud());
    response.setHora(empresa.getHoraGeneracionReparto().getHour());
    response.setMinuto(empresa.getHoraGeneracionReparto().getMinute());

    return response;
  }

}
