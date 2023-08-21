package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.stream.Collectors;

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
import com.example.aquatrack_backend.repo.UsuarioRepo;

@Service
public class UsuarioServicioImpl {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UsuarioRepo usuarioRepo;

  public LoginResponseDTO login(String direccionEmail, String contraseña) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
    String jwt = jwtUtils.generateJwtToken(authentication);
    return LoginResponseDTO.builder().token(jwt).build();
  }

  public CurrentUserDTO getCurrentUser() throws FailedToAuthenticateUserException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext()
          .getAuthentication()
          .getPrincipal();
      Usuario usuario = usuarioRepo.findById(userDetails.getUsuario().getId()).get();
      Empleado empleado = (Empleado) usuario.getPersona();
      List<String> permisos = userDetails.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toList());
      return CurrentUserDTO.builder()
          .nombre(usuario.getPersona().getNombre())
          .empresa(empleado.getEmpresa().getNombre())
          .permisos(permisos)
          .build();
    } else {
      throw new FailedToAuthenticateUserException("Error de autenticación. Intente mas tarde.");
    }

  }
}
