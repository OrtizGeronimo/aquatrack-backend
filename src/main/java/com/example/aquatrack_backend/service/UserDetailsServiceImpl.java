package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.PermisoRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepo usuarioRepository;

    @Autowired
    PermisoRepo permisoRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByDireccionEmail(username)
                                        .orElseThrow(() -> new UsernameNotFoundException("El usuario solicitado no fue encontrado"));
        List<String> permisos = permisoRepository.findPermisosByUsuarioId(user.getId());
        return new SecurityUser(user, permisos);
    }

}
