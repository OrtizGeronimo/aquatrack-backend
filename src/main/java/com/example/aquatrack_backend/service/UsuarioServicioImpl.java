package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.UserDetailsImpl;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.PermisoRol;
import com.example.aquatrack_backend.model.RolUsuario;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.model.dto.CurrentUserDTO;
import com.example.aquatrack_backend.model.dto.LoginResponseDTO;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicioImpl {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepo repo;

    public HashMap<String, String> login(String usuario, String contraseña) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario, contraseña));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

//        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        HashMap<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return response;
    }

    public CurrentUserDTO getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Usuario usuario = repo.findById(userDetails.getId()).get();

            CurrentUserDTO response = new CurrentUserDTO();
            response.setNombre(usuario.getPersona().getNombre());

            Empleado empleado = (Empleado) usuario.getPersona();
            response.setEmpresa(empleado.getNombre());


            List<String> permisos = new ArrayList<>();

            for (RolUsuario rol : usuario.getRolesUsuario()){
                for (PermisoRol permiso: rol.getRol().getPermisos()) {
                    if (!permisos.contains(permiso.getPermiso().getDescripcion())){
                        permisos.add(permiso.getPermiso().getDescripcion());
                    }
                }
            }

            response.setPermisos(permisos);


            return response;
        } else {
            throw new RuntimeException("No se pudo autenticar el usuario");
        }

    }
}
