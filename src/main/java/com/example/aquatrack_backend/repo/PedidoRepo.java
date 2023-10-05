package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepo extends RepoBase<Pedido> {

    @Query(value = "SELECT * FROM pedido INNER JOIN (" +
            "SELECT id as idD FROM domicilio INNER JOIN (" +
            "SELECT id as idC FROM cliente c " +
            "WHERE empresa_id = :idEmpresa" +
            ") c ON c.idC = domicilio.cliente_id" +
            ") d ON d.idD = pedido.domicilio_id " +
            "WHERE tipo_pedido_id = 1", nativeQuery = true)
    Page<Pedido> getAllPedidosExtraordinarios(@Param("idEmpresa")Long idEmpresa, Pageable pageable);


    @Query(value = "SELECT * FROM pedido " +
            "WHERE domicilio_id = :idDomicilio " +
            "AND tipo_pedido_id = 0 " +
            "AND fecha_fin_vigencia is null", nativeQuery = true)
    Pedido getClientPedido(@Param("idDomicilio") Long idDomicilio);
}
