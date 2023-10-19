package com.example.aquatrack_backend.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.dto.ClienteDTO;
import com.example.aquatrack_backend.dto.ClienteListDTO;
import com.example.aquatrack_backend.dto.CodigoDTO;
import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.dto.GuardarClienteDTO;
import com.example.aquatrack_backend.dto.GuardarClienteWebDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.dto.ValidarDniDTO;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Domicilio;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Ubicacion;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.EstadoClienteRepo;
import com.example.aquatrack_backend.repo.EstadoUsuarioRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import com.example.aquatrack_backend.validators.ClientValidator;

@Service
public class ClienteServicio extends ServicioBaseImpl<Cliente> {

  @Autowired
  private ClienteRepo clienteRepo;
  @Autowired
  private CodigoTemporalServicio codigoTemporalServicio;
  @Autowired
  private EmpresaRepo empresaRepo;
  @Autowired
  private UsuarioRepo usuarioRepo;
  @Autowired
  private EstadoUsuarioRepo estadoUsuarioRepo;
  @Autowired
  private EstadoClienteRepo estadoClienteRepo;
  private ModelMapper modelMapper = new ModelMapper();

  @Autowired
  private ClientValidator clientValidator;

  public ClienteServicio(RepoBase<Cliente> repoBase) {
    super(repoBase);
  }

  public Page<ClienteListDTO> findAll(int page, int size, String texto, boolean mostrarInactivos) {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    return clienteRepo
        .findAllByEmpresaPaged(empresa.getId(), texto, mostrarInactivos, paging)
        .map(cliente -> ClienteListDTO.builder()
            .id(cliente.getId())
            .nombreCompleto(cliente.getNombre() + " " + cliente.getApellido())
            .fechaCreacion(cliente.getFechaCreacion())
            .fechaFinVigencia(cliente.getFechaFinVigencia())
            .dni(cliente.getDni().toString())
            .direccionEmail(cliente.getUsuario() == null ? "" : cliente.getUsuario().getDireccionEmail())
            .numTelefono(cliente.getNumTelefono())
            .domicilio(cliente.getDomicilio() == null ? ""
                : cliente.getDomicilio().getCalle() + " "
                    + nullableToEmptyString(cliente.getDomicilio().getNumero()) + " "
                    + nullableToEmptyString(cliente.getDomicilio().getPisoDepartamento())
                    + ", " + nullableToEmptyString(cliente.getDomicilio().getLocalidad()))
            .build());
  }

  public Cliente findClientById(Long idCliente) throws RecordNotFoundException {
    return clienteRepo.findById(idCliente).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
  }

  @Transactional
  public void disableCliente(Long id) throws Exception {
    Cliente clienteDeshabilitado = clienteRepo.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
    clienteRepo.save(clienteDeshabilitado);
  }

  @Transactional
  public ClienteListDTO enableCliente(Long id) throws RecordNotFoundException {
    Cliente cliente = clienteRepo.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    cliente.setFechaFinVigencia(null);
    clienteRepo.save(cliente);
        return ClienteListDTO.builder()
        .id(cliente.getId())
        .dni(cliente.getDni().toString())
        .nombreCompleto(cliente.getNombre() + " " + cliente.getApellido())
        .fechaCreacion(cliente.getFechaCreacion())
        .numTelefono(cliente.getNumTelefono())
        .domicilio(cliente.getDomicilio() == null ? ""
            : cliente.getDomicilio().getCalle() + " "
                + nullableToEmptyString(cliente.getDomicilio().getNumero()) + " "
                + nullableToEmptyString(cliente.getDomicilio().getPisoDepartamento()))
        .build();
  }

  @Transactional
  public EmpresaDTO altaEmpresa(CodigoDTO codigo) throws RecordNotFoundException {

    Long empresa_id = codigoTemporalServicio.obtenerEmpresaPorCodigo(codigo.getCodigo());
    Empresa empresa = empresaRepo.findById(empresa_id)
        .orElseThrow(() -> new RecordNotFoundException("La empresa solicitado no fue encontrado"));

    codigoTemporalServicio.eliminarCodigoUtilizado(codigo.getCodigo());

    EmpresaDTO empresaDTO = new EmpresaDTO();
    empresaDTO.setId(empresa_id);
    empresaDTO.setNombreEmpresa(empresa.getNombre());
    return empresaDTO;
  }

  @Transactional
  public ClienteDTO validarDni(ValidarDniDTO validacion) throws RecordNotFoundException {
    ClienteDTO clienteDTO = new ClienteDTO();
    Cliente cliente = clienteRepo.findByDni(validacion.getDni());
    if (cliente != null) {
      clienteDTO = modelMapper.map(cliente, ClienteDTO.class);
      Domicilio domicilio = cliente.getDomicilio();
      clienteDTO.setCalle(domicilio.getCalle());
      clienteDTO.setNumero(domicilio.getNumero());
      clienteDTO.setPisoDepto(domicilio.getPisoDepartamento());
      clienteDTO.setObservaciones(domicilio.getObservaciones());
    } else {
      clienteDTO.setDni(validacion.getDni());
    }
    clienteDTO.setEmpresaId(validacion.getEmpresaId());
    clienteDTO.setUsuarioId(validacion.getUsuarioId());
    return clienteDTO;
  }

  @Transactional
  public UbicacionDTO createClientFromApp(GuardarClienteDTO cliente) throws RecordNotFoundException {
    Empresa empresa = empresaRepo.findById(cliente.getEmpresaId())
        .orElseThrow(() -> new RecordNotFoundException("No se encontro la empresa"));
    Usuario usuario = usuarioRepo.findById(cliente.getUsuarioId())
        .orElseThrow(() -> new RecordNotFoundException("No se encontro el usuario"));
    Cliente clienteNuevo = new ModelMapper().map(cliente, Cliente.class);
    clienteNuevo.setEmpresa(empresa);
    clienteNuevo.setUsuario(usuario);
    Domicilio domicilio = new Domicilio();
    UbicacionDTO ubicacionDTO = new UbicacionDTO();
    if (cliente.getId() != null) {
      Cliente clienteExist = clienteRepo.findById(cliente.getId())
          .orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
      domicilio = clienteExist.getDomicilio();
      Ubicacion ubicacion = domicilio.getUbicacion();
      ubicacionDTO = modelMapper.map(ubicacion, UbicacionDTO.class);
    } else {
      clienteNuevo.setEstadoCliente(estadoClienteRepo.findByNombreEstadoCliente("En proceso de creación")
              .orElseThrow(()->new RecordNotFoundException("El estado no fue encontrado.")));
    }
    domicilio.setCalle(cliente.getCalle());
    domicilio.setNumero(cliente.getNumero());
    domicilio.setPisoDepartamento(cliente.getPisoDepartamento());
    domicilio.setObservaciones(cliente.getObservaciones());
    clienteNuevo.setDomicilio(domicilio);
    domicilio.setCliente(clienteNuevo);
    Cliente client = clienteRepo.save(clienteNuevo);
    ubicacionDTO.setIdCliente(client.getId());
    return ubicacionDTO;
  }

  @Transactional
  public ClienteListDTO createFromWeb(GuardarClienteWebDTO cliente) throws ClienteNoValidoException {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    clientValidator.validateWebClient(cliente, empresa);

    Cliente clienteNuevo = new Cliente();
    clienteNuevo.setNombre(cliente.getNombre());
    clienteNuevo.setApellido(cliente.getApellido());
    clienteNuevo.setDni(cliente.getDni());
    clienteNuevo.setNumTelefono(cliente.getNumTelefono());
    clienteNuevo.setEmpresa(empresa);

    Domicilio domicilio = new Domicilio();
    domicilio.setCalle(cliente.getCalle());
    domicilio.setNumero(cliente.getNumero());
    domicilio.setPisoDepartamento(cliente.getPisoDepartamento());
    domicilio.setObservaciones(cliente.getObservaciones());
    domicilio.setLocalidad(cliente.getLocalidad());
  
    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setLatitud(cliente.getLatitud());
    ubicacion.setLongitud(cliente.getLongitud());
    domicilio.setUbicacion(ubicacion);
  
    clienteNuevo.setDomicilio(domicilio);
    domicilio.setCliente(clienteNuevo);

    clienteRepo.save(clienteNuevo);
    return ClienteListDTO.builder()
        .id(clienteNuevo.getId())
        .dni(clienteNuevo.getDni().toString())
        .nombreCompleto(clienteNuevo.getNombre() + " " + clienteNuevo.getApellido())
        .fechaCreacion(clienteNuevo.getFechaCreacion())
        .numTelefono(clienteNuevo.getNumTelefono())
        .domicilio(clienteNuevo.getDomicilio() == null ? ""
            : clienteNuevo.getDomicilio().getCalle() + " "
                + nullableToEmptyString(clienteNuevo.getDomicilio().getNumero()) + " "
                + nullableToEmptyString(clienteNuevo.getDomicilio().getPisoDepartamento())
                + ", " + nullableToEmptyString(clienteNuevo.getDomicilio().getLocalidad()))
        .build();
  }

  @Transactional
  public ClienteListDTO updateFromWeb(Long id, GuardarClienteWebDTO cliente) throws RecordNotFoundException, EntidadNoValidaException {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Cliente clienteUpdate = clienteRepo.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clientValidator.validateWebClientUpdate(cliente, empresa);
    clienteUpdate.setNombre(cliente.getNombre());
    clienteUpdate.setApellido(cliente.getApellido());
    clienteUpdate.setDni(cliente.getDni());
    clienteUpdate.setNumTelefono(cliente.getNumTelefono());
    clienteUpdate.setEmpresa(empresa);
    
    clienteUpdate.getDomicilio().setCalle(cliente.getCalle());
    clienteUpdate.getDomicilio().setNumero(cliente.getNumero());
    clienteUpdate.getDomicilio().setPisoDepartamento(cliente.getPisoDepartamento());
    clienteUpdate.getDomicilio().setObservaciones(cliente.getObservaciones());
    clienteUpdate.getDomicilio().setLocalidad(cliente.getLocalidad());
  
    clienteUpdate.getDomicilio().getUbicacion().setLatitud(cliente.getLatitud());
    clienteUpdate.getDomicilio().getUbicacion().setLongitud(cliente.getLongitud());

    clienteRepo.save(clienteUpdate);
    return ClienteListDTO.builder()
        .id(clienteUpdate.getId())
        .dni(clienteUpdate.getDni().toString())
        .nombreCompleto(clienteUpdate.getNombre() + " " + clienteUpdate.getApellido())
        .fechaCreacion(clienteUpdate.getFechaCreacion())
        .numTelefono(clienteUpdate.getNumTelefono())
        .domicilio(clienteUpdate.getDomicilio() == null ? ""
            : clienteUpdate.getDomicilio().getCalle() + " "
                + nullableToEmptyString(clienteUpdate.getDomicilio().getNumero()) + " "
                + nullableToEmptyString(clienteUpdate.getDomicilio().getPisoDepartamento())
                + ", " + nullableToEmptyString(clienteUpdate.getDomicilio().getLocalidad()))
        .build();
  }

  @Transactional
  public GuardarClienteWebDTO clienteForEdit(Long id) throws RecordNotFoundException{
    Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("No se encontro el cliente"));
    return GuardarClienteWebDTO.builder()
    .id(cliente.getId())
    .dni(cliente.getDni())
    .nombre(cliente.getNombre())
    .apellido(cliente.getApellido())
    .numTelefono(cliente.getNumTelefono())
    .calle(cliente.getDomicilio().getCalle())
    .numero(cliente.getDomicilio().getNumero())
    .pisoDepartamento(cliente.getDomicilio().getPisoDepartamento())
    .observaciones(cliente.getDomicilio().getObservaciones())
    .latitud(cliente.getDomicilio().getUbicacion().getLatitud())
    .longitud(cliente.getDomicilio().getUbicacion().getLongitud())
    .localidad(cliente.getDomicilio().getLocalidad())
    .build();
  }

  private String nullableToEmptyString(Object value) {
    if (value == null) {
      return "";
    } else {
      return value.toString();
    }
  }
}