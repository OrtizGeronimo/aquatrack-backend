package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedioPagoRepo extends JpaRepository<MedioPago, Long> {

}
