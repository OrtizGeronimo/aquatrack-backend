package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.PedidoExtraordinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoExtraordinarioRepo extends JpaRepository<PedidoExtraordinario, Long> {
}
