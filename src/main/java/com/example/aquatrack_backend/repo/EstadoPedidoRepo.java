package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.EstadoPedido;
import com.example.aquatrack_backend.model.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoPedidoRepo extends RepoBase<EstadoPedido> {

    EstadoPedido findByNombreEstadoPedido(String nombre);

    @Query(value = "SELECT * FROM estado_pedido " +
            "WHERE fecha_fin_vigencia IS NULL", nativeQuery = true)
    List<EstadoPedido> findAllActives();
}
