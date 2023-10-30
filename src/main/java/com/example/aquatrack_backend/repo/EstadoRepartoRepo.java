package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoReparto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepartoRepo extends RepoBase<EstadoReparto> {

    EstadoReparto findByNombre(String nombre);
}
