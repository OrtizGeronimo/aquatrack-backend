package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutaServicio extends ServicioBaseImpl<Ruta> {

  @Autowired
  private RutaRepo rutaRepo;
  @Autowired
  private EmpleadoRepo empleadoRepo;
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
    response.setRepartidores(empleadoRepo.findEmpleadoByTipoId(2l).stream().map(empleado -> mapper.map(empleado, EmpleadoDTO.class)).collect(Collectors.toList()));

    return response;
  }


  public RutaDTO crearRuta(List<Long> domicilios, List<Long> dias, Long repartidorId, String nombre) throws RecordNotFoundException {


    Ruta ruta = new Ruta();

    List<DomicilioRuta> domiciliosNuevos = new ArrayList<>();
    for (Long index: domicilios) {
        Domicilio domicilio = domicilioRepo.findById(index).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + index));
        DomicilioRuta domicilioRuta = new DomicilioRuta();
        domicilioRuta.setRuta(ruta);
        domicilioRuta.setDomicilio(domicilio);
        domiciliosNuevos.add(domicilioRuta);
    }

    ruta.setDomicilioRutas(domiciliosNuevos);

    List<DiaRuta> diaRutas = new ArrayList<>();

    for (Long index : dias){
      DiaSemana diaSemana = diaSemanaRepo.findById(index).get();
      DiaRuta diaRuta = new DiaRuta();
      diaRuta.setRuta(ruta);
      diaRuta.setDiaSemana(diaSemana);
      diaRutas.add(diaRuta);
    }

    ruta.setDiaRutas(diaRutas);

    Empleado repartidor = empleadoRepo.findById(repartidorId).orElseThrow(() -> new RecordNotFoundException("No se encontró un domicilio con el id " + repartidorId));

    ruta.setRepartidor(repartidor);
    ruta.setNombre(nombre);

    return mapper.map(ruta, RutaDTO.class);
  }

  private String nullableToEmptyString(Object value) {
    if (value == null) {
      return "";
    } else {
      return value.toString();
    }
  }
}
