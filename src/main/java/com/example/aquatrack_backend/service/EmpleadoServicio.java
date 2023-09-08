package com.example.aquatrack_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.dto.EmpleadoDTO;
import com.example.aquatrack_backend.dto.GuardarEmpleadoDTO;
import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.dto.ProductoDTO;
import com.example.aquatrack_backend.dto.TipoEmpleadoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Precio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.model.RolUsuario;
import com.example.aquatrack_backend.model.TipoEmpleado;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.repo.RolRepo;
import com.example.aquatrack_backend.repo.TipoEmpleadoRepo;

@Service
public class EmpleadoServicio extends ServicioBaseImpl<Empleado> {
  @Autowired
  private EmpleadoRepo empleadoRepo;
  @Autowired
  private TipoEmpleadoRepo tipoEmpleadoRepo;
  @Autowired
  private RolRepo rolRepo;

  public EmpleadoServicio(RepoBase<Empleado> repoBase) {
    super(repoBase);
  }

   public Page<EmpleadoDTO> findAllByEnterprise(int page, int size, String nombre, boolean mostrarInactivos) {
      Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Long id = empresa.getId();
      Pageable paging = PageRequest.of(page, size);
      Page<Empleado> empleados = empleadoRepo.findAllByEnterprise(id, nombre, mostrarInactivos, paging);
      Page<EmpleadoDTO> empleadosDTO = empleados.map(empleado -> {
        EmpleadoDTO empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setId((Long) empleado.getId());
        empleadoDTO.setNombre((String) empleado.getNombre());
        empleadoDTO.setApellido((String) empleado.getApellido());
        empleadoDTO.setLegajo((Integer) empleado.getLegajo());
        empleadoDTO.setFechaFinVigencia((LocalDateTime) empleado.getFechaFinVigencia());
        empleadoDTO.setFechaIngreso((LocalDateTime) empleado.getFechaIngreso());
        empleadoDTO.setTipo((String) empleado.getTipo().getNombre());
        return empleadoDTO;
    });
    return empleadosDTO;
  }

 @Transactional
    public EmpleadoDTO createEmpleado(GuardarEmpleadoDTO empleado) {
        Empleado empleadoNuevo = new Empleado();
        Usuario usuarioNuevo = new Usuario();
        RolUsuario rolUsuario = new RolUsuario();
        Rol rolEmpleado = rolRepo.
        rolUsuario.setUsuario(usuarioNuevo);
        rolUsuario.setRol(rolEmpleado);
        usuarioNuevo.setDireccionEmail(empleado.getEmail());
        usuarioNuevo.setFechaCreacion(LocalDateTime.now());
        empleadoNuevo.setNombre(empleado.getNombre());
        empleadoNuevo.setApellido(empleado.getApellido());
        empleadoNuevo.setLegajo(empleado.getLegajo());
        empleadoNuevo.setNumTelefono(empleado.getNumTelefono());
        empleadoNuevo.setFechaCreacion(LocalDateTime.now());
        empleadoNuevo.setFechaIngreso(empleado.getFechaIngreso());
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        empleadoNuevo.setEmpresa(empresa);
        TipoEmpleado tipo = tipoEmpleadoRepo.findById(empleado.getTipoId()).orElseThrow(() -> new RecordNotFoundException("El tipo de empleado solicitado no fue encontrado"));
        empleadoNuevo.setTipo(tipo);
        empleadoRepo.save(empleadoNuevo);
        EmpleadoDTO empleadoDTO = new ModelMapper().map(empleadoNuevo, EmpleadoDTO.class);
        productoDTO.setPrecio(precioNuevo.getPrecio());
        return empleadoDTO;
    }  

  public List<TipoEmpleadoDTO> findAllTiposActive(){
     return tipoEmpleadoRepo.findAllActive()
        .stream()
        .map(tipoEmpleado -> new ModelMapper().map(tipoEmpleado, TipoEmpleadoDTO.class))
        .collect(Collectors.toList());
  }

}
