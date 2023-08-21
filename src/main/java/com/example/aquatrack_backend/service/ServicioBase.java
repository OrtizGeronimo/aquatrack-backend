package com.example.aquatrack_backend.service;

import org.springframework.security.core.context.SecurityContextHolder;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.model.Usuario;

public abstract class ServicioBase {
  protected Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }
}
