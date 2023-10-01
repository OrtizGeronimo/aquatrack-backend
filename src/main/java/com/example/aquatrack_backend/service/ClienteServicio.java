package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserWithOneRolePresentException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class ClienteServicio extends ServicioBaseImpl<Cliente> {

  @Autowired
  private ClienteRepo clienteRepo;
  @Autowired
  private CodigoTemporalServicio codigoTemporalServicio;
  @Autowired
  private EmpresaRepo empresaRepo;
  @Autowired
  private UsuarioServicio usuarioServicio;
  @Autowired
  private UsuarioRepo usuarioRepo;
  private ModelMapper modelMapper = new ModelMapper();

  public ClienteServicio(RepoBase<Cliente> repoBase) {
    super(repoBase);
  }

  public Page<ClienteDTO> findAll(int page, int size, String texto, boolean mostrarInactivos) {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    Page<ClienteDTO> clienteDTOS = clienteRepo.findAllByEmpresaPaged(empresa.getId(), texto, mostrarInactivos, paging).map(cliente -> new ModelMapper().map(cliente, ClienteDTO.class));
    return clienteDTOS;
  }

  public Cliente findClientById(Long idCliente)throws RecordNotFoundException{
    return clienteRepo.findById(idCliente).orElseThrow(()->new RecordNotFoundException("No se encontro el cliente"));
  }

  @Transactional
  public ClienteDTO updateCliente(GuardarClienteDTO cliente, Long id) throws RecordNotFoundException {
    Cliente clienteModificado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteModificado.setNombre(cliente.getNombre());
    clienteModificado.setApellido(cliente.getApellido());
    clienteModificado.setDni(cliente.getDni());
    clienteModificado.setNumTelefono(cliente.getNumTelefono());

    clienteRepo.save(clienteModificado);
    return modelMapper.map(clienteModificado, ClienteDTO.class);
  }

  @Transactional
  public void disableCliente(Long id) throws Exception {
    Cliente clienteDeshabilitado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteDeshabilitado.setFechaFinVigencia(LocalDate.now());
    clienteRepo.save(clienteDeshabilitado);
  }

  @Transactional
  public ClienteDTO enableCliente(Long id) throws RecordNotFoundException {
    Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    cliente.setFechaFinVigencia(null);
    clienteRepo.save(cliente);
    return modelMapper.map(cliente, ClienteDTO.class);
  }

  @Transactional
  public EmpresaDTO altaEmpresa(CodigoDTO codigo) throws RecordNotFoundException{
    Long empresa_id = codigoTemporalServicio.obtenerEmpresaPorCodigo(codigo.getCodigo());
    Empresa empresa = empresaRepo.findById(empresa_id).orElseThrow(() -> new RecordNotFoundException("La empresa solicitado no fue encontrado"));

    EmpresaDTO empresaDTO = new EmpresaDTO();
    empresaDTO.setId(empresa_id);
    empresaDTO.setNombre(empresa.getNombre());
    return empresaDTO;
  }

  @Transactional
  public ClienteDTO validarDni(ValidarDniDTO validacion) throws RecordNotFoundException{
    ClienteDTO clienteDTO = new ClienteDTO();
    Cliente cliente = clienteRepo.findByDni(validacion.getDni());
      if(cliente != null){
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
  public UbicacionDTO createClientFromApp(GuardarClienteDTO cliente) throws RecordNotFoundException{
    Empresa empresa = empresaRepo.findById(cliente.getEmpresaId()).orElseThrow(()->new RecordNotFoundException("No se encontro la empresa"));
    Usuario usuario = usuarioRepo.findById(cliente.getUsuarioId()).orElseThrow(()->new RecordNotFoundException("No se encontro el usuario"));
    Cliente clienteNuevo = new ModelMapper().map(cliente, Cliente.class);
    clienteNuevo.setFechaCreacion(LocalDate.now());
    clienteNuevo.setEmpresa(empresa);
    clienteNuevo.setUsuario(usuario);
    Domicilio domicilio = new Domicilio();
    UbicacionDTO ubicacionDTO = new UbicacionDTO();
    if(cliente.getId() != null){
      Cliente clienteExist = clienteRepo.findById(cliente.getId()).orElseThrow(()->new RecordNotFoundException("No se encontro el cliente"));
      domicilio = clienteExist.getDomicilio();
      Ubicacion ubicacion = domicilio.getUbicacion();
      ubicacionDTO = modelMapper.map(ubicacion, UbicacionDTO.class);
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
  public boolean createFromWeb(GuardarClienteWebDTO cliente){
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Cliente clienteNuevo = new ModelMapper().map(cliente, Cliente.class);
    clienteNuevo.setFechaCreacion(LocalDate.now());
    clienteNuevo.setEmpresa(empresa);
    Domicilio domicilio = new Domicilio();
    domicilio.setCalle(cliente.getCalle());
    domicilio.setNumero(cliente.getNumero());
    domicilio.setPisoDepartamento(cliente.getPisoDepartamento());
    domicilio.setObservaciones(cliente.getObservaciones());
    clienteNuevo.setDomicilio(domicilio);
    clienteRepo.save(clienteNuevo);
    return true;
  }
}