package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoPagoRepo extends JpaRepository<EstadoPago, Long> {

}
