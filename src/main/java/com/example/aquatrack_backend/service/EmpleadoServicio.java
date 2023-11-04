package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserUnauthorizedException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
            empleadoDTO.setFechaFinVigencia(empleado.getFechaFinVigencia());
            empleadoDTO.setFechaIngreso(empleado.getFechaIngreso());
            empleadoDTO.setTipo((String) empleado.getTipo().getNombre());
            return empleadoDTO;
        });
        return empleadosDTO;
    }

    @Transactional
    public EmpleadoDTO createEmpleado(GuardarEmpleadoDTO empleado) throws RecordNotFoundException, EntidadNoValidaException {
        Empleado empleadoNuevo = new Empleado();
        Usuario usuarioNuevo = new Usuario();
        if (usuarioRepo.findByDireccionEmail(empleado.getUsuario().getDireccionEmail()).isPresent()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("usuario.direccionEmail", "Ya existe un usuario con la dirección de mail ingresada.");
            throw new EntidadNoValidaException(errors);
        }
        TipoEmpleado tipo = tipoEmpleadoRepo.findById(empleado.getTipo()).orElseThrow(() -> new RecordNotFoundException("El tipo de empleado solicitado no fue encontrado"));
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        usuarioNuevo.setDireccionEmail(empleado.getUsuario().getDireccionEmail());
        usuarioNuevo.setContraseña(encoder.encode(empleado.getUsuario().getContrasenia()));
        List<RolUsuario> roles = new ArrayList<>();
        for (Long idRol : empleado.getUsuario().getRoles()) {
            Rol rol = rolRepo.findById(idRol).get();
            RolUsuario nuevoRolUsuario = new RolUsuario();
            nuevoRolUsuario.setRol(rol);
            nuevoRolUsuario.setUsuario(usuarioNuevo);
            roles.add(nuevoRolUsuario);
        }
        usuarioNuevo.setRolesUsuario(roles);
        usuarioNuevo.setValidado(false);
        empleadoNuevo.setNombre(empleado.getNombre());
        empleadoNuevo.setApellido(empleado.getApellido());
        empleadoNuevo.setLegajo(empleado.getLegajo());
        empleadoNuevo.setNumTelefono(empleado.getNumTelefono());
        empleadoNuevo.setFechaIngreso(empleado.getFechaIngreso());
        empleadoNuevo.setEmpresa(empresa);
        empleadoNuevo.setTipo(tipo);

        usuarioRepo.save(usuarioNuevo);
        empleadoNuevo.setUsuario(usuarioNuevo);
        empleadoRepo.save(empleadoNuevo);
        EmpleadoDTO empleadoDTO = new ModelMapper().map(empleadoNuevo, EmpleadoDTO.class);
        empleadoDTO.setTipo(tipo.getNombre());
        return empleadoDTO;
    }

    public List<TipoEmpleadoDTO> findAllTiposActive() {
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
        List<Long> roles = new ArrayList<>();
        for (RolUsuario rol : rolesUsuario) {
            roles.add(rol.getRol().getId());
        }
        response.getUsuario().setRoles(roles);

        return response;
    }

    public EmpleadoDTO update(Long id, GuardarEmpleadoDTO empleado) throws RecordNotFoundException, EntidadNoValidaException {
        Empleado empleadoExistente = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado no fue encontrado"));

        if (!empleado.getUsuario().getDireccionEmail().equals(empleadoExistente.getUsuario().getDireccionEmail()) && usuarioRepo.findByDireccionEmail(empleado.getUsuario().getDireccionEmail()).isPresent()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("usuario.direccionEmail", "Ya existe un usuario con la dirección de mail ingresada.");
            throw new EntidadNoValidaException(errors);
        }

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

        for (Long rol : rolesNuevos) {
            RolUsuario rolUsuario = new RolUsuario();
            rolUsuario.setUsuario(usuarioRepo.findById(empleadoExistente.getUsuario().getId()).get());
            rolUsuario.setRol(rolRepo.findById(rol).get());
            empleadoExistente.getUsuario().getRolesUsuario().add(rolUsuario);
        }

        Empleado empleadoGuardado = empleadoRepo.save(empleadoExistente);


        String tipo = empleadoGuardado.getTipo().getNombre();

        EmpleadoDTO empleadoDTO = mapper.map(empleadoGuardado, EmpleadoDTO.class);
        empleadoDTO.setTipo(tipo);
        return empleadoDTO;

    }

    @Transactional
    public void disable(Long id) throws Exception {
        Empleado empleadoDeshabilitado = empleadoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El empleado solicitado no fue encontrado"));
        Usuario usuarioDeshabilitado = empleadoDeshabilitado.getUsuario();
        usuarioDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
        empleadoDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
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
        EmpleadoDTO response = new ModelMapper().map(empleadoRehabilitado, EmpleadoDTO.class);
        response.setTipo(empleadoRehabilitado.getTipo().getNombre());
        return response;
    }

    @Transactional
    public DetalleRepartidorMobileDTO infoRepartidor() throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Empleado repartidor = (Empleado) persona;
        if (persona instanceof Cliente || !repartidor.getTipo().getNombre().equals("Repartidor")) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para repartidores.");
        }

        return DetalleRepartidorMobileDTO.builder()
                .nombre(repartidor.getNombre())
                .apellido(repartidor.getApellido())
                .empresa(repartidor.getEmpresa().getNombre())
                .numTelefono(repartidor.getNumTelefono())
                .legajo(repartidor.getLegajo())
                .empresa(repartidor.getEmpresa().getNombre())
                .direccionEmail(repartidor.getUsuario().getDireccionEmail())
                .build();
    }

    @Transactional
    public EditarRepartidorMobileDTO editarRepartidorMobile(EditarRepartidorMobileDTO atributos) throws UserUnauthorizedException {
        Persona persona = getUsuarioFromContext().getPersona();
        Empleado repartidor = (Empleado) persona;
        if (persona instanceof Cliente || !repartidor.getTipo().getNombre().equals("Repartidor")) {
            throw new UserUnauthorizedException("Esta funcionalidad es exclusiva para repartidores.");
        }

        repartidor.setNombre(atributos.getNombre());
        repartidor.setApellido(atributos.getApellido());
        repartidor.setNumTelefono(atributos.getNumTelefono());
        empleadoRepo.save(repartidor);

        EditarRepartidorMobileDTO respuesta = new EditarRepartidorMobileDTO();
        respuesta.setNombre(repartidor.getNombre());
        respuesta.setApellido(repartidor.getApellido());
        respuesta.setNumTelefono(repartidor.getNumTelefono());
        return respuesta;
    }
}
