package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserWithOneRolePresentException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.EmpresaRepo;
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
  public EmpresaDTO altaEmpresa(CodigoDTO codigo) throws RecordNotFoundException{
    Long empresa_id = codigoTemporalServicio.obtenerEmpresaPorCodigo(codigo.getCodigo());
    Empresa empresa = empresaRepo.findById(empresa_id).orElseThrow(() -> new RecordNotFoundException("La empresa solicitado no fue encontrado"));

    EmpresaDTO empresaDTO = new EmpresaDTO();
    empresaDTO.setId(empresa_id);
    empresaDTO.setNombre(empresa.getNombre());
    return empresaDTO;
  }

  @Transactional
  public ClienteDTO createFromApp(GuardarClienteDTO cliente, Long empresa_id) throws RecordNotFoundException{
    Long id = clienteRepo.findByDni(cliente.getDni());
    if(id != null){
      return updateCliente(cliente, id);
    }
    Empresa empresa = empresaRepo.findById(empresa_id).orElseThrow(()->new RecordNotFoundException("No se encontro la empresa"));
    Cliente clienteNuevo = new Cliente();
    clienteNuevo.setNombre(cliente.getNombre());
    clienteNuevo.setApellido(cliente.getApellido());
    clienteNuevo.setDni(cliente.getDni());
    clienteNuevo.setNumTelefono(cliente.getNum_telefono());
    List<EmpresaCliente> empresaClientes = new ArrayList<>();
    empresaClientes.add(new EmpresaCliente(empresa, clienteNuevo));
    clienteNuevo.setEmpresaClientes(empresaClientes);
    clienteRepo.save(clienteNuevo);
    return new ModelMapper().map(clienteNuevo, ClienteDTO.class);
  }

  @Transactional
  public boolean altaNuevaEmpresa(Long idC, CodigoDTO codigo) throws RecordNotFoundException{
   Cliente cliente = clienteRepo.findById(idC).orElseThrow(() -> new RecordNotFoundException("El cliente no fue encontrado"));
   Long idE = codigoTemporalServicio.obtenerEmpresaPorCodigo(codigo.getCodigo());
   Empresa empresa = empresaRepo.findById(idE).orElseThrow(() -> new RecordNotFoundException("La empresa no fue encontrada"));
   List<EmpresaCliente> empresaClientes = cliente.getEmpresaClientes();
   empresaClientes.add(new EmpresaCliente(empresa, cliente));
   cliente.setEmpresaClientes(empresaClientes);
   clienteRepo.save(cliente);
   return true;
  }
}
