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
  private UbicaciónServicio ubicaciónServicio;
  @Autowired
  private ClienteServicio clienteServicio;
  private ModelMapper mapper = new ModelMapper();

  public DomicilioServicio(RepoBase<Domicilio> repoBase) {
    super(repoBase);
  }

  public boolean crearDomicilioUbicacion(UbicacionDTO ubicacionDTO) throws RecordNotFoundException {
    Cliente cliente = clienteServicio.findClientById(ubicacionDTO.getIdCliente());
    Domicilio domicilio = domicilioRepo.findById(cliente.getDomicilio().getId()).orElseThrow(() -> new RecordNotFoundException("No se encontro el domicilio"));
    Ubicacion ubicacion = ubicaciónServicio.guardarUbicacion(ubicacionDTO);
    domicilio.setUbicacion(ubicacion);
    domicilioRepo.save(domicilio);
    return true;
  }

  public UbicacionDTO getDomicilioUbicacion(Long id){
    Ubicacion ubicacion = domicilioRepo.findDomicilioUbi(id);
    UbicacionDTO ubicacionDTO = new ModelMapper().map(ubicacion, UbicacionDTO.class);
    return ubicacionDTO;
  }
}
