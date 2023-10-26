package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.ClienteNoCubiertoApp;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import com.example.aquatrack_backend.validators.ClientValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomicilioServicio extends ServicioBaseImpl<Domicilio> {
  @Autowired
  private DomicilioRepo domicilioRepo;
  @Autowired
  private UbicaciónServicio ubicaciónServicio;
  @Autowired
  private ClienteServicio clienteServicio;
  @Autowired
  private EstadoClienteRepo estadoClienteRepo;
  @Autowired
  private EstadoUsuarioRepo estadoUsuarioRepo;
  private ModelMapper mapper = new ModelMapper();
  private ClientValidator clientValidator = new ClientValidator();

  public DomicilioServicio(RepoBase<Domicilio> repoBase) {
    super(repoBase);
  }

  public boolean crearDomicilioUbicacion(UbicacionDTO ubicacionDTO) throws RecordNotFoundException, ClienteNoCubiertoApp {
    Cliente cliente = clienteServicio.findClientById(ubicacionDTO.getIdCliente());
    Empresa empresa = cliente.getEmpresa();
    clientValidator.validateAppClient(ubicacionDTO, empresa.getCobertura());
    Domicilio domicilio = cliente.getDomicilio();
    Ubicacion ubicacion = ubicaciónServicio.guardarUbicacion(ubicacionDTO);
    domicilio.setUbicacion(ubicacion);
    domicilio.setLocalidad(ubicacionDTO.getLocalidad());
    cliente.setEstadoCliente(estadoClienteRepo.findByNombreEstadoCliente("Habilitado")
            .orElseThrow(()-> new RecordNotFoundException("El estado habilitado no fue encontrado.")));
    cliente.getUsuario().setEstadoUsuario(estadoUsuarioRepo.findByNombreEstadoUsuario("Habilitado")
            .orElseThrow(()-> new RecordNotFoundException("El estado habilitado no fue encontrado.")));
    domicilioRepo.save(domicilio);
    return true;
  }

  public UbicacionDTO getDomicilioUbicacion(Long id){
    Ubicacion ubicacion = domicilioRepo.findDomicilioUbi(id);
    UbicacionDTO ubicacionDTO = new ModelMapper().map(ubicacion, UbicacionDTO.class);
    return ubicacionDTO;
  }

  public Pedido getPedidoHabitual(Domicilio domicilio){
    return domicilio.getPedidos().stream().filter(pedido -> (pedido.getFechaFinVigencia() == null) && (pedido.getTipoPedido().getId() == 1)).findFirst().get();
  }
}
