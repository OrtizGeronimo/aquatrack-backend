package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.DomicilioDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.repo.ClienteRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.repo.DomicilioRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class DomicilioServicio extends ServicioBaseImpl<Domicilio> {
  @Autowired
  private DomicilioRepo domicilioRepo;
  @Autowired
  private ClienteRepo clienteRepo;
  private ModelMapper mapper = new ModelMapper();

  public DomicilioServicio(RepoBase<Domicilio> repoBase) {
    super(repoBase);
  }

  public boolean crearDomicilioUbicacion(UbicacionDTO ubicacionDTO, Long idDomicilio) throws RecordNotFoundException {
    Domicilio domicilio = domicilioRepo.findById(idDomicilio).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
    domicilio.setUbicacion(mapper.map(ubicacionDTO, Ubicacion.class));
    domicilioRepo.save(domicilio);
    return true;
  }

/*  public Domicilio editarDomicilioManual(DomicilioDTO domicilioDTO, Long idDomicilio) throws RecordNotFoundException{
    Domicilio domicilio = domicilioRepo.findById(idDomicilio).orElseThrow(() -> new RecordNotFoundException("El domicilio no fue encontrado"));
    Domicilio domicilioNuevo = mapper.map(domicilio, );
  }*/
  public UbicacionDTO getDomicilioUbicacion(Long id){
    Ubicacion ubicacion = domicilioRepo.findDomicilioUbi(id);
    UbicacionDTO ubicacionDTO = new ModelMapper().map(ubicacion, UbicacionDTO.class);
    return ubicacionDTO;
  }
}
