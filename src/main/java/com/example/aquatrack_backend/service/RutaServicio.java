package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutaServicio extends ServicioBaseImpl<Ruta> {

  @Autowired
  private RutaRepo rutaRepo;
  @Autowired
  private DiaSemanaRepo diaSemanaRepo;
  @Autowired
  private DomicilioRepo domicilioRepo;

  private ModelMapper mapper = new ModelMapper();


  public RutaServicio(RepoBase<Ruta> repoBase) {
    super(repoBase);
  }

  public RutaFormDTO newRuta(){

    RutaFormDTO response = new RutaFormDTO();

    List<DomicilioDTO> domicilios = new ArrayList<>();

    for (Domicilio domicilio: domicilioRepo.findAll()) {
      DomicilioDTO domicilioDTO = new DomicilioDTO();
      domicilioDTO.setDomicilio(domicilio.getCalle() + nullableToEmptyString(domicilio.getNumero()) + nullableToEmptyString(domicilio.getPisoDepartamento()));
      domicilioDTO.setNombreApellidoCliente(domicilio.getCliente().getNombre() + " " + domicilio.getCliente().getApellido());
      domicilioDTO.setId(domicilio.getId());
      domicilios.add(domicilioDTO);
    }

    response.setDomicilios(domicilios);
    response.setDias(diaSemanaRepo.findAll().stream().map(dia -> mapper.map(dia, DiaSemanaDTO.class)).collect(Collectors.toList()));

    return response;
  }

  public Page<RutaListDTO> findAll(int page, int size, Long idDiaSemana, String texto, boolean mostrarInactivos) {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    return rutaRepo
            .findAllByEmpresaPaged(empresa.getId(), texto, idDiaSemana, mostrarInactivos, paging)
            .map(ruta -> RutaListDTO.builder()
                    .id(ruta.getId())
                    .nombre(ruta.getNombre())
                    .fechaCreacion(ruta.getFechaCreacion())
                    .idDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()))
                    .domiciliosAVisitar(ruta.getDomicilioRutas().size())
                    .build());
  }

  @Transactional
  public RutaDTO crearRuta(GuardarRutaDTO rutaDTO) throws RecordNotFoundException {

    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Ruta ruta = new Ruta();

    ruta.setEmpresa(empresa);

    List<DiaRuta> diaRutas = new ArrayList<>();

    for (Long id : rutaDTO.getIdDiasSemana()){
      DiaSemana diaSemana = diaSemanaRepo.findById(id).get();
      DiaRuta diaRuta = new DiaRuta();
      diaRuta.setRuta(ruta);
      diaRuta.setDiaSemana(diaSemana);
      diaRutas.add(diaRuta);
    }

    ruta.setDiaRutas(diaRutas);
    ruta.setNombre(rutaDTO.getNombre());

    List<DomicilioRuta> domiciliosNuevos = new ArrayList<>();
    for (DomiciliosRutaDTO domicilioRutaDTO: rutaDTO.getDomiciliosRuta()) {
      Domicilio domicilio = domicilioRepo.findById(domicilioRutaDTO.getIdDomicilio()).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + domicilioRutaDTO.getIdDomicilio()));
      DomicilioRuta domicilioRuta = new DomicilioRuta();
      domicilioRuta.setRuta(ruta);
      domicilioRuta.setDomicilio(domicilio);
      domiciliosNuevos.add(domicilioRuta);

      List<DiaDomicilio> diaDomicilios = new ArrayList<>();
      for (Long id: domicilioRutaDTO.getIdDiasSemana()) {
        DiaDomicilio diaDomicilio = new DiaDomicilio();
        diaDomicilio.setDomicilio(domicilio);
        DiaRuta diaRuta = diaRutas.stream()
                .filter(dia -> dia.getDiaSemana().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("El dia de la semana de ruta no fue encontrado"));
        diaDomicilio.setDiaRuta(diaRuta);
        diaDomicilios.add(diaDomicilio);
      }
      domicilio.setDiaDomicilios(diaDomicilios);
    }

    ruta.setDomicilioRutas(domiciliosNuevos);
    Ruta rutaGuardada = rutaRepo.save(ruta);
    return mapper.map(rutaGuardada, RutaDTO.class);
  }

  private String nullableToEmptyString(Object value) {
    if (value == null) {
      return "";
    } else {
      return value.toString();
    }
  }

  public ResponseDetalleRutaDTO detalleRuta(Long id) throws RecordNotFoundException {

    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id" + id));

    ResponseDetalleRutaDTO response = new ResponseDetalleRutaDTO();

    response.setId(ruta.getId());
    response.setNombre(ruta.getNombre());



    List<DetalleRutaDTO> rutas = new ArrayList<>();

    for (DiaRuta dia : ruta.getDiaRutas()) {
      List<DomicilioDetalleDTO> domicilios = new ArrayList<>();
      DetalleRutaDTO rutaDTO = new DetalleRutaDTO();
      rutaDTO.setDia(dia.getDiaSemana().getNombre());
      rutaDTO.setIdDia(dia.getDiaSemana().getId());
      for (DomicilioRuta domicilio : ruta.getDomicilioRutas()) {
        if (domicilio.getDomicilio().getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().equals(dia))) {
          DomicilioDetalleDTO domicilioDTO = new DomicilioDetalleDTO();
          domicilioDTO.setDomicilio(domicilio.getDomicilio().getCalle() + " " + nullableToEmptyString(domicilio.getDomicilio().getNumero()) + " " + nullableToEmptyString(domicilio.getDomicilio().getPisoDepartamento()));
          domicilioDTO.setLatitud(domicilio.getDomicilio().getUbicacion().getLatitud());
          domicilioDTO.setLongitud(domicilio.getDomicilio().getUbicacion().getLongitud());
          domicilios.add(domicilioDTO);
        }
      }
      rutaDTO.setDomicilios(domicilios);
      rutas.add(rutaDTO);
    }

    response.setRutas(rutas);

    return response;
  }

}
