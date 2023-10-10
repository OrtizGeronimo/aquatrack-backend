package com.example.aquatrack_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.aquatrack_backend.dto.RegisterRequestDTO;
import com.example.aquatrack_backend.dto.RegisterResponseDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserNoValidoException;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.EstadoUsuarioRepo;
import com.example.aquatrack_backend.repo.RolRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import com.example.aquatrack_backend.validators.UserValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.CurrentUserDTO;
import com.example.aquatrack_backend.dto.LoginResponseDTO;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.exception.ClienteWebUnauthorizedException;
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Persona;
import com.example.aquatrack_backend.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServicio {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private UsuarioRepo usuarioRepo;
  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private RolRepo rolRepo;
  @Autowired
  private EstadoUsuarioRepo estadoUsuarioRepo;
  private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  private UserValidator userValidator = new UserValidator();

  public LoginResponseDTO login(String direccionEmail, String contraseña) throws ClienteWebUnauthorizedException{
    Authentication authentication = authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
    Persona persona = ((SecurityUser) authentication.getPrincipal()).getUsuario().getPersona();
    if (persona instanceof Cliente) {
      throw new ClienteWebUnauthorizedException("No puede iniciar sesión como cliente en AquaTrack Web.");
    }
    String jwt = jwtUtils.generateJwtToken(authentication);
    return LoginResponseDTO.builder().token(jwt).build();
  }

  private Usuario createUser(String mail, String password, String confirmacionPassword){
    Usuario usuario = new Usuario();
    usuario.setDireccionEmail(mail);
    usuario.setContraseña(bCryptPasswordEncoder.encode(password));
    usuario.setConfirmacionContraseña(bCryptPasswordEncoder.encode(confirmacionPassword));
    usuario.setValidado(true);
    usuario.setFechaCreacion(LocalDate.now());
    return usuario;
  }

  @Transactional
  public RegisterResponseDTO clientRegister(RegisterRequestDTO register) throws UserNoValidoException, RecordNotFoundException {
    userValidator.validateClientUser(register.getDireccionEmail());
    Usuario usuario = createUser(register.getDireccionEmail(), register.getContraseña(), register.getConfirmacionContraseña());
    usuario.setEstadoUsuario(estadoUsuarioRepo.findByNombreEstadoUsuario("Creado").orElseThrow(()->new RecordNotFoundException("El estado no fue encontrado.")));
    Rol rol = rolRepo.findClientRole();
    List<RolUsuario> rolUsuarios = new ArrayList<>();
    rolUsuarios.add(new RolUsuario(rol, usuario));
    usuario.setRolesUsuario(rolUsuarios);
    Usuario usuarioGuardado = usuarioRepo.save(usuario);
    return new RegisterResponseDTO(register.getIdEmpresa(), usuarioGuardado.getId());
  }

  public CurrentUserDTO getCurrentUser() throws FailedToAuthenticateUserException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
      Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
      List<String> permisos = userDetails.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toList());
      return CurrentUserDTO.builder()
          .nombre(empleado.getNombre() + " " + empleado.getApellido())
          .empresa(empleado.getEmpresa().getNombre())
          .permisos(permisos)
          .build();
    } else {
      throw new FailedToAuthenticateUserException("Error de autenticación. Intente mas tarde.");
    }
  }

  private Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }
}
