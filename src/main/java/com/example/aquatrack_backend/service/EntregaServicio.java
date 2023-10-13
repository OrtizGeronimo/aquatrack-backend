package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.EntregaRepo;
import com.example.aquatrack_backend.repo.EstadoEntregaRepo;
import com.example.aquatrack_backend.repo.RepartoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.service.ServicioBaseImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class EntregaServicio extends ServicioBaseImpl<Entrega> {

  @Autowired
  private EntregaRepo entregaRepo;
  @Autowired
  private EstadoEntregaRepo estadoEntregaRepo;

  public EntregaServicio(RepoBase<Entrega> repoBase) {
    super(repoBase);
  }

  @Transactional
  public Page<EntregaListDTO> findAllEntregasByReparto(Long idReparto, int page, int size){
    Pageable paging = PageRequest.of(page, size);
    return entregaRepo
            .findAllByRepartoPaged(idReparto, paging)
            .map(entrega -> EntregaListDTO.builder()
                    .id(entrega.getId())
                    .fechaHoraVisita(entrega.getFechaHoraVisita())
                    .estadoEntregaId(entrega.getEstadoEntrega().getId())
                    .build());
  }

  @Transactional
  public EntregaDTO detallarEntrega(Long idEntrega) throws RecordNotFoundException{
    Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(()-> new RecordNotFoundException("No se encontró la entrega"));
    return EntregaDTO.builder()
            .id(entrega.getId())
            .estadoEntrega(
                    EstadoEntregaDTO.builder()
                            .nombreEstadoEntrega(entrega.getEstadoEntrega().getNombreEstadoEntrega())
                            .build()
            )
            .domicilio(
                    DomicilioDTO.builder()
                            .calle(entrega.getDomicilio().getCalle())
                            .numero(entrega.getDomicilio().getNumero())
                            .pisoDepartamento(entrega.getDomicilio().getPisoDepartamento())
                            .observaciones(entrega.getDomicilio().getObservaciones())
                            .build()
            )
            .ordenVisita(entrega.getOrdenVisita())
            .build();
  }

  @Transactional
  public EntregaListDTO disableEntrega(Long idEntrega) throws RecordNotFoundException {
    Entrega entrega = entregaRepo.findById(idEntrega).orElseThrow(()-> new RecordNotFoundException("No se encontró la entrega"));
    entrega.setEstadoEntrega(estadoEntregaRepo.findByNombreEstadoEntrega("Cancelada"));
    entregaRepo.save(entrega);
    return EntregaListDTO.builder()
            .id(entrega.getId())
            .build();
  }

  public boolean procesarEntrega(Long idEntrega){
    return true;
  }

}
