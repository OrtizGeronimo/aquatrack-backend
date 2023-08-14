package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.TipoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEmpleadoRepo extends JpaRepository<TipoEmpleado, Long> {
}
