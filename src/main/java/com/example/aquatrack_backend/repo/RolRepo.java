package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepo extends JpaRepository<Rol, Long> {
}
