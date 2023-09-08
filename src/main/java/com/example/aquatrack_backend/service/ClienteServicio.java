package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.ClienteDTO;
import com.example.aquatrack_backend.dto.GuardarClienteDTO;
import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserWithOneRolePresentException;
import com.example.aquatrack_backend.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServicio extends ServicioBaseImpl<Cliente> {

  @Autowired
  private ClienteRepo clienteRepo;

  public ClienteServicio(RepoBase<Cliente> repoBase) {
    super(repoBase);
  }

  public Page<ClienteDTO> findAll(int page, int size, String nombre, boolean mostrarInactivos) {
    Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
    Pageable paging = PageRequest.of(page, size);
    Page<Cliente> clienteDTOS1 = clienteRepo.findAllByEmpresa(empresa.getId(), paging);
    Page<ClienteDTO> clienteDTOS = clienteRepo.findAllByEmpresa(empresa.getId(),/* nombre, mostrarInactivos,*/ paging).map(cliente -> new ModelMapper().map(cliente, ClienteDTO.class));
    return clienteDTOS;
  }

  @Transactional
  public ClienteDTO updateCliente(GuardarClienteDTO cliente, Long id) throws RecordNotFoundException {
    Cliente clienteModificado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteModificado.setNombre(cliente.getNombre());
    clienteModificado.setApellido(cliente.getApellido());
    clienteModificado.setDni(cliente.getDni());
    clienteModificado.setNumTelefono(cliente.getNum_telefono());

    clienteRepo.save(clienteModificado);
    return new ModelMapper().map(clienteModificado, ClienteDTO.class);
  }

  @Transactional
  public void disableCliente(Long id) throws Exception {
    Cliente clienteDeshabilitado = clienteRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El cliente solicitado no fue encontrado"));
    clienteDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
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
  public ClienteDTO createFromApp(GuardarClienteDTO cliente){
    if(clienteRepo.existsByDni(cliente.getDni()) > 0){
      return new ClienteDTO();
    }
    Cliente clienteNuevo = new Cliente();
    clienteNuevo.setNombre(cliente.getNombre());
    clienteNuevo.setApellido(cliente.getApellido());
    clienteNuevo.setDni(cliente.getDni());
    clienteNuevo.setNumTelefono(cliente.getNum_telefono());
    clienteRepo.save(clienteNuevo);
    return new ModelMapper().map(clienteNuevo, ClienteDTO.class);
  }
}
