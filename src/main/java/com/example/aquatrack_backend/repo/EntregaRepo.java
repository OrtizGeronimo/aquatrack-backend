package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntregaRepo extends JpaRepository<Entrega, Long> {
}
