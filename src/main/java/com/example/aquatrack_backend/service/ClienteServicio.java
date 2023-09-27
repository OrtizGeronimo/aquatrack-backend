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

  public ClienteServicio(RepoBase<Cliente> repoBase) {
    super(repoBase);
  }

  public Page<ClienteDTO> findAll(int page, int size, String texto, boolean mostrarInactivos) {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    Page<ClienteDTO> clienteDTOS = clienteRepo.findAllByEmpresaPaged(empresa.getId(), texto, mostrarInactivos, paging).map(cliente -> new ModelMapper().map(cliente, ClienteDTO.class));
    return clienteDTOS;
  }

  @Transactional
  public ClienteDTO updateCliente(GuardarClienteDTO cliente, Long id) throws RecordNotFoundException {
    Cliente clienteModificado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteModificado.setNombre(cliente.getNombre());
    clienteModificado.setApellido(cliente.getApellido());
    clienteModificado.setDni(cliente.getDni());
    clienteModificado.setNumTelefono(cliente.getNumTelefono());

    clienteRepo.save(clienteModificado);
    return new ModelMapper().map(clienteModificado, ClienteDTO.class);
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
    return new ModelMapper().map(cliente, ClienteDTO.class);
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
      Long id = clienteRepo.findByDni(validacion.getDni());
      if(id != null){
          Cliente cliente = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
          ClienteDTO clienteDto = new ModelMapper().map(cliente, ClienteDTO.class);
          clienteDto.setEmpresaId(validacion.getEmpresaId());
          clienteDto.setUsuarioId(validacion.getUsuarioId());
          clienteDto.setId(clienteDto.getId());
      }
      ClienteDTO clienteDTO = new ClienteDTO();
      clienteDTO.setDni(validacion.getDni());
      clienteDTO.setEmpresaId(validacion.getEmpresaId());
      clienteDTO.setUsuarioId(validacion.getUsuarioId());
      return clienteDTO;
  }

  @Transactional
  public ClienteDTO createClientFromApp(GuardarClienteDTO cliente) throws RecordNotFoundException{
    Empresa empresa = empresaRepo.findById(cliente.getEmpresaId()).orElseThrow(()->new RecordNotFoundException("No se encontro la empresa"));
    Usuario usuario = usuarioRepo.findById(cliente.getUsuarioId()).orElseThrow(()->new RecordNotFoundException("No se encontro el usuario"));
    if(cliente.getId() != null){
      clienteRepo.findById(cliente.getId()).orElseThrow(()->new RecordNotFoundException("No se encontro el cliente"));
    }
    Cliente clienteNuevo = new ModelMapper().map(cliente, Cliente.class);
    clienteNuevo.setFechaCreacion(LocalDate.now());
    clienteNuevo.setEmpresa(empresa);
    clienteNuevo.setUsuario(usuario);
    clienteRepo.save(clienteNuevo);
    return new ModelMapper().map(clienteNuevo, ClienteDTO.class);
  }

/*  @Transactional
  public ClienteDTO createExistingClientFromApp(GuardarClienteDTO cliente, Long id, Long empresaId) throws RecordNotFoundException {
    Cliente clienteModificado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteModificado.setNombre(cliente.getNombre());
    clienteModificado.setApellido(cliente.getApellido());
    clienteModificado.setDni(cliente.getDni());
    clienteModificado.setNumTelefono(cliente.getNumTelefono());
    Usuario usuario = usuarioServicio.createUserClient(cliente.getMail(), cliente.getPassword(), empresaId);
    clienteModificado.setUsuario(usuario);

    clienteRepo.save(clienteModificado);
    return new ModelMapper().map(clienteModificado, ClienteDTO.class);
  }*/
}
