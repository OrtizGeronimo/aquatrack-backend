package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.TipoPedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPedidoRepo extends RepoBase<TipoPedido> {
    TipoPedido getTipoPedidoById(Long id);

    TipoPedido findByNombreTipoPedido(String nombre);

    @Query(value = "SELECT * FROM tipo_pedido " +
            "WHERE fecha_fin_vigencia IS NULL", nativeQuery = true)
    List<TipoPedido> findAllActives();
}
