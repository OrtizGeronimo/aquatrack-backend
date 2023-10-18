package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoUsuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoUsuarioRepo extends RepoBase<EstadoUsuario>{

    Optional<EstadoUsuario> findByNombreEstadoUsuario(String nombre);
}
