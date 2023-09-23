package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.ChangePasswordDTO;
import com.example.aquatrack_backend.dto.CurrentUserDTO;
import com.example.aquatrack_backend.dto.LoginResponseDTO;
import com.example.aquatrack_backend.exception.FailedToAuthenticateUserException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.UsuarioRepo;

@Service
public class UsuarioServicio {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  UsuarioRepo usuarioRepo;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UsuarioServicio(PasswordEncoder passwordEncoder) {
      this.passwordEncoder = passwordEncoder;
  }

  @Autowired
  private JwtUtils jwtUtils;

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

  private Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }

  public ChangePasswordDTO updatePassword(ChangePasswordDTO dto){
    Optional<Usuario> usuarioOpt = usuarioRepo.findByTokenPassword(dto.getTokenPassword());
    Usuario usuario = usuarioOpt.get();
    if (usuarioOpt.isPresent()) {
      String password = passwordEncoder.encode(dto.getPassword());
      usuario.setContraseña(password);
      usuario.setTokenPassword(null);
      usuarioRepo.save(usuario);
      return dto;
    } else {
      throw new IllegalArgumentException("No se encontró el usuario correspondiente al token");
    }
  }

}
