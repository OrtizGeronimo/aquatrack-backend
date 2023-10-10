package com.example.aquatrack_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.aquatrack_backend.dto.RegisterRequestDTO;
import com.example.aquatrack_backend.dto.RegisterResponseDTO;
import com.example.aquatrack_backend.dto.UpdateUserDTO;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.model.RolUsuario;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.RolRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
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
  private EmpleadoRepo empleadoRepo;
  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private RolRepo rolRepo;
  private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  public LoginResponseDTO login(String direccionEmail, String contraseña) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
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
  public RegisterResponseDTO clientRegister(RegisterRequestDTO register){
    Usuario usuario = createUser(register.getDireccionEmail(), register.getContraseña(), register.getConfirmacionContraseña());
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
          .password(userDetails.getPassword())
          .nombre(empleado.getNombre() + " " + empleado.getApellido())
          .empresa(empleado.getEmpresa().getNombre())
          .permisos(permisos)
          .build();
    } else {
      throw new FailedToAuthenticateUserException("Error de autenticación. Intente mas tarde.");
    }
  }

  @Transactional
  public Usuario createUserClient(String email, String password, Long empresaId){
    Usuario usuario = new Usuario();
    usuario.setDireccionEmail(email);
    usuario.setContraseña(bCryptPasswordEncoder.encode(password));
    usuario.setFechaCreacion(LocalDate.now());
    usuario.setValidado(true);
    Rol rol = rolRepo.findClientRole();
    List<RolUsuario> rolUsuarios = new ArrayList<>();
    rolUsuarios.add(new RolUsuario(rol, usuario));
    usuario.setRolesUsuario(rolUsuarios);
    usuarioRepo.save(usuario);
    return usuario;
  }

  @Transactional
  public UpdateUserDTO updateUserProfile(UpdateUserDTO usuarioDTO){
    Usuario usuario = getUsuarioFromContext();
    Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
    usuario.setDireccionEmail(usuarioDTO.getEmail());
    empleado.setApellido(usuarioDTO.getApellido());
    empleado.setNombre(usuarioDTO.getNombre());
    empleado.setNumTelefono(usuarioDTO.getNroTelefono());
    usuarioRepo.save(usuario);
    empleadoRepo.save(empleado);
    return usuarioDTO;
  }

  public UpdateUserDTO getUserProfile(){
    Usuario usuario = getUsuarioFromContext();
    Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
    UpdateUserDTO usuarioDTO = new UpdateUserDTO();
    usuarioDTO.setEmail(usuario.getDireccionEmail());
    usuarioDTO.setApellido(empleado.getApellido());
    usuarioDTO.setNombre(empleado.getNombre());
    usuarioDTO.setNroTelefono(empleado.getNumTelefono());
    return usuarioDTO;
  }

  @Transactional
  public String changePassword(String password){
    Usuario usuario = getUsuarioFromContext();
    usuario.setContraseña(bCryptPasswordEncoder.encode(password));
    usuarioRepo.save(usuario);
    return "Contraseña cambiada con exito";
  }

  private Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }
}
