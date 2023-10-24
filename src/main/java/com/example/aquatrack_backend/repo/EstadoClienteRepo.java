package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoCliente;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoClienteRepo extends RepoBase<EstadoCliente>{

    Optional<EstadoCliente> findByNombreEstadoCliente(String nombre);
}
