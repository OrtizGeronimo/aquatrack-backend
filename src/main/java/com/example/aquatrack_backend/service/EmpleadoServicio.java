package com.example.aquatrack_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.aquatrack_backend.dto.*;
import org.hibernate.criterion.Example;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.aquatrack_backend.repo.RolUsuarioRepo;
import com.example.aquatrack_backend.repo.TipoEmpleadoRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;

@Service
public class EmpleadoServicio extends ServicioBaseImpl<Empleado> {
  @Autowired
  private EmpleadoRepo empleadoRepo;
  @Autowired
  private TipoEmpleadoRepo tipoEmpleadoRepo;
  @Autowired
  private RolRepo rolRepo;
  @Autowired
  private UsuarioRepo usuarioRepo;
  @Autowired
  private RolUsuarioRepo rolUsuarioRepo;

  @Autowired
  private PasswordEncoder encoder;

  private ModelMapper mapper = new ModelMapper();

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
        empleadoDTO.setFechaFinVigencia((LocalDate) empleado.getFechaFinVigencia());
        empleadoDTO.setFechaIngreso((LocalDate) empleado.getFechaIngreso());
        empleadoDTO.setTipo((String) empleado.getTipo().getNombre());
        return empleadoDTO;
    });
    return empleadosDTO;
  }

 @Transactional
    public EmpleadoDTO createEmpleado(GuardarEmpleadoDTO empleado) throws RecordNotFoundException{
        System.out.println("Empleado --------------------------------> " + empleado);
        Empleado empleadoNuevo = new Empleado();
        Usuario usuarioNuevo = new Usuario();
//        RolUsuario rolUsuario = new RolUsuario();
        TipoEmpleado tipo = tipoEmpleadoRepo.findById(empleado.getTipo()).orElseThrow(() -> new RecordNotFoundException("El tipo de empleado solicitado no fue encontrado"));
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
//        Rol rolRepartidor = rolRepo.findByName("Repartidor", empresa.getId());
//        Rol rolOficinista = rolRepo.findByName("Oficinista", empresa.getId());
//        if(tipo.getNombre() == "Repartidor") {
//          rolUsuario.setRol(rolRepartidor);
//        } else {
//          rolUsuario.setRol(rolOficinista);
//        }
         usuarioNuevo.setDireccionEmail(empleado.getUsuario().getDireccionEmail());
         usuarioNuevo.setContraseña(encoder.encode(empleado.getUsuario().getContrasenia()));
         usuarioNuevo.setFechaCreacion(LocalDate.now());
         List<RolUsuario> roles = new ArrayList<>();
         for (Long idRol : empleado.getUsuario().getRoles()) {
             Rol rol = rolRepo.findById(idRol).get();
             RolUsuario nuevoRolUsuario = new RolUsuario();
             nuevoRolUsuario.setRol(rol);
             nuevoRolUsuario.setUsuario(usuarioNuevo);
             roles.add(nuevoRolUsuario);
         }
         usuarioNuevo.setRolesUsuario(roles);
        // rolUsuario.setRol(rolEmpleado);

//        rolUsuario.setUsuario(usuarioNuevo);
        empleadoNuevo.setNombre(empleado.getNombre());
        empleadoNuevo.setApellido(empleado.getApellido());
        empleadoNuevo.setLegajo(empleado.getLegajo());
        empleadoNuevo.setNumTelefono(empleado.getNumTelefono());
        empleadoNuevo.setFechaCreacion(LocalDate.now());
        empleadoNuevo.setFechaIngreso(empleado.getFechaIngreso());
        empleadoNuevo.setEmpresa(empresa);
        empleadoNuevo.setTipo(tipo);

        usuarioRepo.save(usuarioNuevo);
        empleadoNuevo.setUsuario(usuarioNuevo);
//        rolUsuarioRepo.save(rolUsuario);
        empleadoRepo.save(empleadoNuevo);
        EmpleadoDTO empleadoDTO = new ModelMapper().map(empleadoNuevo, EmpleadoDTO.class);
        empleadoDTO.setTipo(tipo.getNombre());
        return empleadoDTO;
    }  

  public List<TipoEmpleadoDTO> findAllTiposActive(){
     return tipoEmpleadoRepo.findAllActive()
        .stream()
        .map(tipoEmpleado -> mapper.map(tipoEmpleado, TipoEmpleadoDTO.class))
        .collect(Collectors.toList());
  }

    public EmpleadoDetailDTO detail(Long id) throws RecordNotFoundException {
      Empleado empleado = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado no fue encontrado"));
      List<RolUsuario> rolesUsuario = empleado.getUsuario().getRolesUsuario();
      Long idTipo = empleado.getTipo().getId();
      empleado.setTipo(null);
      empleado.getUsuario().setRolesUsuario(null);
      EmpleadoDetailDTO response = mapper.map(empleado, EmpleadoDetailDTO.class);
      response.setTipo(idTipo);
      response.getUsuario().setRoles(null);
//      List<RolDTO> roles = new ArrayList<>();
//        for (RolUsuario rol : empleado.getUsuario().getRolesUsuario()) {
//            roles.add(mapper.map(rol.getRol(), RolDTO.class));
//        }
//      response.getUsuario().setRoles(roles);
        List<Long> roles = new ArrayList<>();
        for (RolUsuario rol : rolesUsuario) {
            roles.add(rol.getRol().getId());
        }
      response.getUsuario().setRoles(roles);

      return response;
  }

  public EmpleadoDTO update(Long id, GuardarEmpleadoDTO empleado) throws RecordNotFoundException {

      Empleado empleadoExistente = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado no fue encontrado"));

      empleadoExistente.setTipo(tipoEmpleadoRepo.findById(empleado.getTipo()).get());

      empleadoExistente.setLegajo(empleado.getLegajo());
      empleadoExistente.setFechaIngreso(empleado.getFechaIngreso());
      empleadoExistente.setNombre(empleado.getNombre());
      empleadoExistente.setApellido(empleado.getApellido());
      empleadoExistente.setNumTelefono(empleado.getNumTelefono());
      empleadoExistente.getUsuario().setDireccionEmail(empleado.getUsuario().getDireccionEmail());
      empleadoExistente.setFechaIngreso(empleado.getFechaIngreso());

      List<Long> rolesActuales = empleadoExistente.getUsuario().getRolesUsuario().stream().map(rolUsuario -> rolUsuario.getRol().getId()).collect(Collectors.toList());

      List<Long> rolesRevocados = rolesActuales.stream().filter(idRol -> !empleado.getUsuario().getRoles().contains(idRol)).collect(Collectors.toList());

      List<Long> rolesNuevos = empleado.getUsuario().getRoles().stream().filter(idRol -> !rolesActuales.contains(idRol)).collect(Collectors.toList());

      empleadoExistente.getUsuario().getRolesUsuario().removeIf(rolUsuario -> rolesRevocados.contains(rolUsuario.getRol().getId()));

      for (Long rol: rolesNuevos) {
          RolUsuario rolUsuario = new RolUsuario();
          rolUsuario.setUsuario(usuarioRepo.findById(empleadoExistente.getUsuario().getId()).get());
          rolUsuario.setRol(rolRepo.findById(rol).get());
          empleadoExistente.getUsuario().getRolesUsuario().add(rolUsuario);
      }

      Empleado empleadoGuardado = empleadoRepo.save(empleadoExistente);


      String tipo = empleadoGuardado.getTipo().getNombre();

      EmpleadoDTO empleadoDTO = mapper.map(empleadoGuardado, EmpleadoDTO.class);
      empleadoDTO.setTipo(tipo);
      return mapper.map(empleadoGuardado, EmpleadoDTO.class);

  }

    @Transactional
    public void disable(Long id) throws Exception {
        Empleado empleadoDeshabilitado = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado solicitado no fue encontrado"));
        Usuario usuarioDeshabilitado = empleadoDeshabilitado.getUsuario();
        usuarioDeshabilitado.setFechaFinVigencia(LocalDate.now());
        empleadoDeshabilitado.setFechaFinVigencia(LocalDate.now());
        empleadoRepo.save(empleadoDeshabilitado);
        usuarioRepo.save(usuarioDeshabilitado);
    }

    @Transactional
    public EmpleadoDTO enable(Long id) throws RecordNotFoundException {
        Empleado empleadoRehabilitado = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado solicitado no fue encontrado"));
        empleadoRehabilitado.setFechaFinVigencia(null);
        empleadoRepo.save(empleadoRehabilitado);
        Usuario usuario = empleadoRehabilitado.getUsuario();
        usuario.setFechaFinVigencia(null);
        usuarioRepo.save(usuario);
        return new ModelMapper().map(empleadoRehabilitado, EmpleadoDTO.class);
    }
}
