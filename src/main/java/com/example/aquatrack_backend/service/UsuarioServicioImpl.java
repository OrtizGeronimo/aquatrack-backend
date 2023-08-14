package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.UserDetailsImpl;
import com.example.aquatrack_backend.model.dto.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicioImpl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponseDTO login(String usuario, String contraseña) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario, contraseña));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUsuario(userDetails.getUsername());
        response.setContraseña(userDetails.getPassword());
        response.setRoles(roles);
        response.setToken(jwt);
        return response;
    }
}
