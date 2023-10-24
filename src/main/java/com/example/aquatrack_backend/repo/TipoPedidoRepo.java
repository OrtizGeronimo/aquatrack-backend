package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPedidoRepo extends RepoBase<TipoPedido> {

    TipoPedido findByNombreTipoPedido(String nombre);
}
