package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.validators.EmpresaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
public class EmpresaServicio extends ServicioBaseImpl<Empresa> {
  @Autowired
  private EmpresaRepo empresaRepo;

  @Autowired
  private EmpresaValidator empresaValidator;

  public EmpresaServicio(RepoBase<Empresa> repoBase) {
    super(repoBase);
  }

  public EmpresaDTO detalleEmpresa() {

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

  public EmpresaDTO editarEmpresa(EmpresaDTO dto) throws EntidadNoValidaException {

    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

    empresaValidator.validateEmpresa(dto, empresa.getCobertura());

    empresa.setNombre(dto.getNombreEmpresa());
    Ubicacion ubicacion = empresa.getUbicacion();
    ubicacion.setLatitud(dto.getLatitud());
    ubicacion.setLongitud(dto.getLongitud());
    empresa.setUbicacion(ubicacion);
    empresa.setEmail(dto.getMailEmpresa());
    LocalTime horario = LocalTime.of(dto.getHora(), dto.getMinuto());
    empresa.setHoraGeneracionReparto(horario);
    empresa.setUrl(dto.getUrlEmpresa());
    empresa.setNumTelefono(dto.getNumTelefono());

    empresaRepo.save(empresa);

    return detalleEmpresa();

  }
}
