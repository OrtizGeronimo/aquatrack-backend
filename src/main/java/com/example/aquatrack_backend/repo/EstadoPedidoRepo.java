package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoPedidoRepo extends RepoBase<EstadoPedido> {

    EstadoPedido findByNombreEstadoPedido(String nombre);
}
