package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolUsuarioRepo extends JpaRepository<RolUsuario, Long> {
}
