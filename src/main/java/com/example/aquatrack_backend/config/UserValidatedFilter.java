package com.example.aquatrack_backend.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.aquatrack_backend.dto.ErrorResponseDTO;
import com.example.aquatrack_backend.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserValidatedFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Usuario usuario = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getUsuario();
    if (!usuario.getValidado()) {
      response.setHeader("Content-Type", "application/json");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter()
          .write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
              .message("No puede realizar esta acción si su usuario no está validado.")
              .build()));
      return;
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getRequestURI();
    return Arrays.asList("/users/login",
        "/users/register",
        "/coberturas/conocer_cercana",
        "/clientes/app",
        "/clientes/codigo",
        "/clientes/dni",
        "/email/sendEmailConfirm",
        "/users/confirmEmail",
        "/users/login/mobile",
        "/domicilios/ubicacion", "/forgot-password", "/email/sendPasswordEmail", "/users/changePassword",
        "/users/changePassword/**", "/change-password/**", "/users/current", "/users/current/mobile")
        .stream().anyMatch(p -> p.equals(path));
  }
}
