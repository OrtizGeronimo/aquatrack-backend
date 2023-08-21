package com.example.aquatrack_backend.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.aquatrack_backend.model.Usuario;

public class SecurityUser implements UserDetails {

  private final Usuario usuario;

  private final List<String> permisos;

  public SecurityUser(Usuario usuario, List<String> permisos) {
    this.usuario = usuario;
    this.permisos = permisos;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return permisos.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return usuario.getContraseña();
  }

  @Override
  public String getUsername() {
    return usuario.getDireccionEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return usuario.getFechaFinVigencia() == null;
  }
}
