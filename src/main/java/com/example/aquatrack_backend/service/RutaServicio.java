package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.ValidacionException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
  @Autowired
  private DiaDomicilioRepo diaDomicilioRepo;
  @Autowired
  private DiaRutaRepo diaRutaRepo;

  @Autowired
  private ClienteRepo clienteRepo;

  private ModelMapper mapper = new ModelMapper();


  public RutaServicio(RepoBase<Ruta> repoBase) {
    super(repoBase);
  }

  public RutaFormDTO newRuta(){

    RutaFormDTO response = new RutaFormDTO();

    List<DomicilioDTO> domicilios = new ArrayList<>();

    for (Domicilio domicilio: domicilioRepo.findAllActivos(false)) {
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
                    .fechaFinVigencia(ruta.getFechaFinVigencia())
                    .idDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()))
                    .domiciliosAVisitar(ruta.getDomicilioRutas().size())
                    .build());
  }

  @Transactional(rollbackFor = ValidacionException.class)
  public RutaListDTO crearRuta(GuardarRutaDTO rutaDTO) throws RecordNotFoundException, ValidacionException {

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

    Ruta rutaGuardada = rutaRepo.save(ruta);

    List<DomicilioRuta> domiciliosNuevos = new ArrayList<>();
    for (DomiciliosRutaDTO domicilioRutaDTO: rutaDTO.getDomiciliosRuta()) {
      Domicilio domicilio = domicilioRepo.findById(domicilioRutaDTO.getIdDomicilio()).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + domicilioRutaDTO.getIdDomicilio()));
      DomicilioRuta domicilioRuta = new DomicilioRuta();
      domicilioRuta.setRuta(rutaGuardada);
      domicilioRuta.setDomicilio(domicilio);
      domiciliosNuevos.add(domicilioRuta);

      List<DiaDomicilio> diaDomicilios = new ArrayList<>();
      for (Long id: domicilioRutaDTO.getIdDiasSemana()) {
        if (domicilio.getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId().equals(id) && !diaDomicilio.getDiaRuta().getRuta().getId().equals(rutaGuardada.getId()))) {
          HashMap<String, String> errors = new HashMap<>();
          errors.put("root", "El domicilio " + domicilio.getId() + " ya forma parte de una ruta la cual pasa por el dia " + diaSemanaRepo.findById(id).get().getNombre() );
          throw new ValidacionException(errors);
        }
        DiaDomicilio diaDomicilio = new DiaDomicilio();
        diaDomicilio.setDomicilio(domicilio);
        DiaRuta diaRuta = diaRutas.stream()
                .filter(dia -> dia.getDiaSemana().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("El dia de la semana de ruta no fue encontrado"));
        diaDomicilio.setDiaRuta(diaRuta);
        diaDomicilios.add(diaDomicilio);
      }
      domicilio.getDiaDomicilios().addAll(diaDomicilios);
    }

    rutaGuardada.setDomicilioRutas(domiciliosNuevos);
    Ruta rutaResponse = rutaRepo.save(rutaGuardada);
    RutaListDTO response = RutaListDTO.builder()
            .id(rutaResponse.getId())
            .nombre(rutaResponse.getNombre())
            .fechaCreacion(rutaResponse.getFechaCreacion())
            .idDiasSemana(rutaResponse.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()))
            .domiciliosAVisitar(rutaResponse.getDomicilioRutas().size())
            .fechaFinVigencia(rutaResponse.getFechaFinVigencia())
            .build();
      return response;
//    return mapper.map(rutaGuardada, RutaListDTO.class);
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
          domicilioDTO.setId(domicilio.getId());
          domicilioDTO.setDomicilio(domicilio.getDomicilio().getCalle() + " " + nullableToEmptyString(domicilio.getDomicilio().getNumero()) + " " + nullableToEmptyString(domicilio.getDomicilio().getPisoDepartamento()));
          domicilioDTO.setLatitud(domicilio.getDomicilio().getUbicacion().getLatitud());
          domicilioDTO.setLongitud(domicilio.getDomicilio().getUbicacion().getLongitud());
          domicilioDTO.setNombreCliente(domicilio.getDomicilio().getCliente().getNombre() + " " + domicilio.getDomicilio().getCliente().getApellido());
          domicilios.add(domicilioDTO);
        }
      }
      rutaDTO.setDomicilios(domicilios);
      rutas.add(rutaDTO);
    }

    response.setRutas(rutas);

    return response;
  }


  @Transactional
  public ResponseDetalleRutaDTO editarDiasRuta(Long id, GuardarRutaDTO dto) throws RecordNotFoundException {

    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    ruta.setNombre(dto.getNombre());

    List<Long> idDiasActuales = ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList());

    List<DiaRuta> dias = new ArrayList<>();

    for (Long idDia : dto.getIdDiasSemana()) {
        if (idDiasActuales.contains(idDia)){
          dias.add(ruta.getDiaRutas().stream().filter(diaRuta -> diaRuta.getDiaSemana().getId().equals(idDia)).findFirst().get());
          continue;
        }
        DiaRuta diaRuta = new DiaRuta();
        diaRuta.setRuta(ruta);
        diaRuta.setDiaSemana(diaSemanaRepo.findById(idDia).get());
        dias.add(diaRuta);
    }

    for (DiaRuta dia: ruta.getDiaRutas().stream().filter(diaRuta -> !dias.contains(diaRuta)).collect(Collectors.toList())) {
      ruta.getDiaRutas().remove(dia);
    }
    ruta.getDiaRutas().clear();
    ruta.getDiaRutas().addAll(dias);

    Ruta rutaGuardada = rutaRepo.save(ruta);

    return detalleRuta(rutaGuardada.getId());
  }

  @Transactional
  public ResponseDetalleRutaDTO asignarClientesRuta(Long idRuta, GuardarRutaDTO rutaDTO) throws RecordNotFoundException, ValidacionException {

    Ruta ruta = rutaRepo.findById(idRuta).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + idRuta));

    List<DomicilioRuta> domiciliosNuevos = new ArrayList<>();
    for (DomiciliosRutaDTO domicilioRutaDTO: rutaDTO.getDomiciliosRuta()) {
      Domicilio domicilio = domicilioRepo.findById(domicilioRutaDTO.getIdDomicilio()).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + domicilioRutaDTO.getIdDomicilio()));
      DomicilioRuta domicilioRuta = new DomicilioRuta();
      domicilioRuta.setRuta(ruta);
      domicilioRuta.setDomicilio(domicilio);
      domiciliosNuevos.add(domicilioRuta);

      List<DiaDomicilio> diaDomicilios = new ArrayList<>();
      for (Long id: domicilioRutaDTO.getIdDiasSemana()) {
        if (domicilio.getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId().equals(id) && !diaDomicilio.getDiaRuta().getRuta().getId().equals(ruta.getId()))) {
          HashMap<String, String> errors = new HashMap<>();
          errors.put("root", "El domicilio " + domicilio.getId() + " ya forma parte de una ruta la cual pasa por el dia " + diaSemanaRepo.findById(id).get().getNombre() );
          throw new ValidacionException(errors);
        }
        DiaDomicilio diaDomicilio = new DiaDomicilio();
        diaDomicilio.setDomicilio(domicilio);
        DiaRuta diaRuta = ruta.getDiaRutas().stream()
                .filter(dia -> dia.getDiaSemana().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("El dia de la semana de ruta no fue encontrado"));
        diaDomicilio.setDiaRuta(diaRuta);
        diaDomicilios.add(diaDomicilio);
      }
      domicilio.getDiaDomicilios().addAll(diaDomicilios);
    }

    ruta.getDomicilioRutas().addAll(domiciliosNuevos);

    Ruta rutaGuardada = rutaRepo.save(ruta);

    ResponseDetalleRutaDTO response = new ResponseDetalleRutaDTO();

    response.setId(rutaGuardada.getId());
    response.setNombre(rutaGuardada.getNombre());


    List<DetalleRutaDTO> rutas = new ArrayList<>();

    for (DiaRuta dia : rutaGuardada.getDiaRutas()) {
      List<DomicilioDetalleDTO> domicilios = new ArrayList<>();
      DetalleRutaDTO rutaResponseDTO = new DetalleRutaDTO();
      rutaResponseDTO.setDia(dia.getDiaSemana().getNombre());
      rutaResponseDTO.setIdDia(dia.getDiaSemana().getId());
      for (DomicilioRuta domicilio : ruta.getDomicilioRutas()) {
        if (domicilio.getDomicilio().getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().equals(dia))) {
          DomicilioDetalleDTO domicilioDTO = new DomicilioDetalleDTO();
          domicilioDTO.setId(domicilio.getId());
          domicilioDTO.setDomicilio(domicilio.getDomicilio().getCalle() + " " + nullableToEmptyString(domicilio.getDomicilio().getNumero()) + " " + nullableToEmptyString(domicilio.getDomicilio().getPisoDepartamento()));
          domicilioDTO.setLatitud(domicilio.getDomicilio().getUbicacion().getLatitud());
          domicilioDTO.setLongitud(domicilio.getDomicilio().getUbicacion().getLongitud());
          domicilioDTO.setNombreCliente(domicilio.getDomicilio().getCliente().getNombre() + " " + domicilio.getDomicilio().getCliente().getApellido());
          domicilios.add(domicilioDTO);
        }
      }
      rutaResponseDTO.setDomicilios(domicilios);
      rutas.add(rutaResponseDTO);
    }

    response.setRutas(rutas);
//    RutaListDTO response = RutaListDTO.builder()
//            .id(ruta.getId())
//            .nombre(ruta.getNombre())
//            .fechaCreacion(ruta.getFechaCreacion())
//            .idDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()))
//            .domiciliosAVisitar(ruta.getDomicilioRutas().size())
//            .fechaFinVigencia(ruta.getFechaFinVigencia())
//            .build();
    return response;

  }

  @Transactional
  public ResponseDetalleRutaDTO editarClientesRuta(Long id, GuardarRutaDTO dto) throws RecordNotFoundException, ValidacionException {

    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    List<DomicilioRuta> domicilios = ruta.getDomicilioRutas();

    List<DomicilioRuta> domiciliosNuevos = new ArrayList<>();

    for (DomiciliosRutaDTO domicilioDTO: dto.getDomiciliosRuta()) {

      Domicilio domicilio = domicilioRepo.findById(domicilioDTO.getIdDomicilio()).get();

      List<Long> dias = domicilioDTO.getIdDiasSemana();

      List<DiaDomicilio> diasNuevos = domicilio.getDiaDomicilios().stream().filter(diaDomicilio -> !diaDomicilio.getDiaRuta().getRuta().equals(ruta)).collect(Collectors.toList());

      for (Long dia : dias) {
        if (domicilio.getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId().equals(dia) && diaDomicilio.getDiaRuta().getRuta().equals(ruta))){
          diasNuevos.add(domicilio.getDiaDomicilios().stream().filter(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId().equals(dia) && diaDomicilio.getDiaRuta().getRuta().equals(ruta)).findFirst().get());
          continue;
        }
        if (domicilio.getDiaDomicilios().stream().anyMatch(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId().equals(dia) && !diaDomicilio.getDiaRuta().getRuta().equals(ruta))) {
          HashMap<String, String> errors = new HashMap<>();
          errors.put("root", "El domicilio " + domicilio.getId() + " ya forma parte de una ruta la cual pasa por el dia " + diaSemanaRepo.findById(id).get().getNombre() );
          throw new ValidacionException(errors);
        }
        DiaDomicilio diaDomicilio = new DiaDomicilio();
        diaDomicilio.setDomicilio(domicilio);
        DiaRuta diaRuta = ruta.getDiaRutas().stream()
                .filter(diaActual -> diaActual.getDiaSemana().getId().equals(dia))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("El dia de la semana de ruta no fue encontrado"));
        diaDomicilio.setDiaRuta(diaRuta);
        diasNuevos.add(diaDomicilio);
      }

//      for (DiaDomicilio diaDomicilio : domicilio.getDiaDomicilios().stream().filter(diaDomicilio -> !diasNuevos.contains(diaDomicilio)).collect(Collectors.toList())) {
//        domicilio.getDiaDomicilios().remove(diaDomicilio);
//      }

      domicilio.getDiaDomicilios().clear();
      domicilio.getDiaDomicilios().addAll(diasNuevos);

      if (domicilios.stream().anyMatch(domicilioRuta -> domicilioRuta.getDomicilio().getId().equals(domicilioDTO.getIdDomicilio()))){
        DomicilioRuta domicilioExistente = domicilios.stream().filter(domicilioRuta -> domicilioRuta.getDomicilio().getId().equals(domicilioDTO.getIdDomicilio())).findFirst().get();
        domicilioExistente.setDomicilio(domicilio);
        domiciliosNuevos.add(domicilioExistente);
        continue;
      }

      DomicilioRuta nuevoDomicilio = new DomicilioRuta();
      nuevoDomicilio.setRuta(ruta);
      nuevoDomicilio.setDomicilio(domicilio);
      domiciliosNuevos.add(nuevoDomicilio);
    }

    for (DomicilioRuta domicilioRuta: ruta.getDomicilioRutas().stream().filter(domicilioRuta -> !dto.getDomiciliosRuta().stream().map(DomiciliosRutaDTO::getIdDomicilio).collect(Collectors.toList()).contains(domicilioRuta.getDomicilio().getId())).collect(Collectors.toList())) {
      List<DiaDomicilio> diaDomicilios = domicilioRuta.getDomicilio().getDiaDomicilios().stream().filter(diaDomicilio -> diaDomicilio.getDiaRuta().getRuta().equals(ruta)).collect(Collectors.toList());
      for (DiaDomicilio diaDomicilio: diaDomicilios) {
        diaDomicilioRepo.deleteById(diaDomicilio.getId());
      }
    }

    ruta.getDomicilioRutas().clear();
    ruta.getDomicilioRutas().addAll(domiciliosNuevos);

    rutaRepo.save(ruta);

  return detalleRuta(ruta.getId());
  }

  public GuardarRutaDTO edit(Long id) throws RecordNotFoundException {
    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    GuardarRutaDTO response = new GuardarRutaDTO();

    response.setId(ruta.getId());
    response.setNombre(ruta.getNombre());
    response.setIdDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()));
    response.setDomiciliosRuta(ruta.getDomicilioRutas().stream().map(domicilioRuta -> {
      DomiciliosRutaDTO domicilio = new DomiciliosRutaDTO();
      domicilio.setNombreCliente(domicilioRuta.getDomicilio().getCliente().getNombre() + " " + domicilioRuta.getDomicilio().getCliente().getApellido());
      domicilio.setIdDomicilio(domicilioRuta.getDomicilio().getId());
      domicilio.setDomicilio(obtenerDescripcionDomicilio(domicilioRuta.getDomicilio()));
      domicilio.setIdDiasSemana(domicilioRuta.getDomicilio().getDiaDomicilios().stream().filter(diaDomicilio -> diaDomicilio.getDiaRuta().getRuta().equals(ruta)).map(diaDomicilio -> diaDomicilio.getDiaRuta().getDiaSemana().getId()).collect(Collectors.toList()));
      return domicilio;
    }).collect(Collectors.toList()));

    return response;

  }

  public AsignarClientesRutaDTO clientes(Long id) throws RecordNotFoundException {
    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();

    List<DomicilioProjection> domiciliosProjection = rutaRepo.buscarClientesAjenos(id, empresa.getId());

    List<Domicilio> domicilios = domiciliosProjection.stream().map(domicilioProjection -> {
      Domicilio domicilio = new Domicilio();
      domicilio.setId(domicilioProjection.getId());
      domicilio.setCalle(domicilioProjection.getCalle());
      domicilio.setNumero(domicilioProjection.getNumero());
      domicilio.setPisoDepartamento(domicilioProjection.getPisoDepartamento());
      domicilio.setCliente(clienteRepo.findById(domicilioProjection.getCliente()).get());
      return domicilio;
    }).filter(domicilio -> ruta.getDomicilioRutas().stream().noneMatch(domicilioRuta -> domicilioRuta.getDomicilio().getId().equals(domicilio.getId()))).collect(Collectors.toList());

    List<DomicilioDetalleDTO> domiciliosADevolver = domicilios.stream().map( domicilio -> {
      DomicilioDetalleDTO domicilioDTO = new DomicilioDetalleDTO();
      domicilioDTO.setDomicilio(domicilio.getCalle() + " " +
              nullableToEmptyString(domicilio.getNumero()) +
              " " +
              nullableToEmptyString(domicilio.getPisoDepartamento()));
      domicilioDTO.setId(domicilio.getId());
      domicilioDTO.setNombreCliente(domicilio.getCliente().getNombre() + " " + domicilio.getCliente().getApellido());
      return domicilioDTO;
    }).collect(Collectors.toList());

    AsignarClientesRutaDTO response = new AsignarClientesRutaDTO();

    GuardarRutaDTO rutaDTO = new GuardarRutaDTO();
    rutaDTO.setNombre(ruta.getNombre());
    rutaDTO.setId(ruta.getId());
    rutaDTO.setFechaFinVigencia(ruta.getFechaFinVigencia());
    rutaDTO.setIdDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()));
    response.setRuta(rutaDTO);
    response.setDomicilios(domiciliosADevolver);

    return response;
  }

  public String obtenerDescripcionDomicilio(Domicilio domicilio){
    return domicilio.getCalle() + " " + nullableToEmptyString(domicilio.getNumero()) + " " + nullableToEmptyString(domicilio.getPisoDepartamento());
  }

  public void deshabilitar(Long id) throws RecordNotFoundException {
    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    ruta.setFechaFinVigencia(LocalDateTime.now());

    rutaRepo.save(ruta);

  }

  public RutaListDTO habilitar(Long id) throws RecordNotFoundException {
    Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontró una ruta con el id " + id));

    ruta.setFechaFinVigencia(null);

    rutaRepo.save(ruta);
    RutaListDTO response = RutaListDTO.builder()
            .id(ruta.getId())
            .nombre(ruta.getNombre())
            .fechaCreacion(ruta.getFechaCreacion())
            .idDiasSemana(ruta.getDiaRutas().stream().map(diaRuta -> diaRuta.getDiaSemana().getId()).collect(Collectors.toList()))
            .domiciliosAVisitar(ruta.getDomicilioRutas().size())
            .fechaFinVigencia(ruta.getFechaFinVigencia())
            .build();
    return response;
  }
}
