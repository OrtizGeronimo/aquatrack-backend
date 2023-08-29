package com.example.aquatrack_backend.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.PermisoRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UsuarioRepo usuarioRepo;

  @Autowired
  PermisoRepo permisoRepo;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario user = usuarioRepo.findByDireccionEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("El usuario solicitado no fue encontrado"));
    List<String> permisos = permisoRepo.findPermisosByUsuarioId(user.getId());
    return new SecurityUser(user, permisos);
  }
}
