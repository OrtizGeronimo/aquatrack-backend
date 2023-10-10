package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoEntregaRepo extends RepoBase<EstadoEntrega> {

    EstadoEntrega findByNombreEstadoEntrega(String nombre);

}
