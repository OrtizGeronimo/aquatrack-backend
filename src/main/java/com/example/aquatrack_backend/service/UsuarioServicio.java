package com.example.aquatrack_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.model.RolUsuario;
import com.example.aquatrack_backend.repo.RolRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.CurrentUserDTO;
import com.example.aquatrack_backend.dto.LoginResponseDTO;
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.model.Empleado;
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

  public LoginResponseDTO login(String direccionEmail, String contraseña) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
    String jwt = jwtUtils.generateJwtToken(authentication);
    return LoginResponseDTO.builder().token(jwt).build();
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

  @Transactional
  public Usuario createUserClient(String email, String password, Long empresaId){
    Usuario usuario = new Usuario();
    usuario.setDireccionEmail(email);
    usuario.setContraseña(password);
    usuario.setFechaCreacion(LocalDate.now());
    usuario.setValidado(true);
    Rol rol = rolRepo.findClientRolByEmpresa(empresaId);
    List<RolUsuario> rolUsuarios = new ArrayList<>();
    rolUsuarios.add(new RolUsuario(rol, usuario));
    usuario.setRolesUsuario(rolUsuarios);
    usuarioRepo.save(usuario);
    return usuario;
  }

  private Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }
}
