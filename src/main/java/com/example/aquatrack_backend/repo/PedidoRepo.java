package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PedidoRepo extends RepoBase<Pedido> {

    @Query(value = "SELECT * FROM pedido INNER JOIN (" +
            "SELECT id as idD FROM domicilio INNER JOIN (" +
            "SELECT id as idC FROM cliente c " +
            "WHERE empresa_id = :idEmpresa " +
            "AND (:nombreCliente IS NULL OR nombre LIKE %:nombreCliente%)" +
            ") c ON c.idC = domicilio.cliente_id" +
            ") d ON d.idD = pedido.domicilio_id " +
            "WHERE estado_pedido_id = :estadoPedido " +
            "AND tipo_pedido_id = :tipoPedido " +
            "AND (:fechaCoordinadaDesde IS NULL OR fecha_coordinada_entrega >= :fechaCoordinadaDesde) " +
            "AND (:fechaCoordinadaHasta IS NULL OR fecha_coordinada_entrega <= :fechaCoordinadaHasta) " +
            "AND (:mostrar_inactivos = true OR fecha_fin_vigencia IS NULL)" +
            "ORDER BY fecha_coordinada_entrega ASC", nativeQuery = true)
    Page<Pedido> getAllPedidos(@Param("idEmpresa") Long idEmpresa,
                               Pageable pageable,
                               @Param("mostrar_inactivos") boolean mostrarInactivos,
                               @Param("nombreCliente") String nombreCliente,
                               @Param("estadoPedido") Long estadoPedido,
                               @Param("tipoPedido") Long tipoPedido,
                               @Param("fechaCoordinadaDesde") LocalDateTime fechaCoordinadaDesde,
                               @Param("fechaCoordinadaHasta") LocalDateTime fechaCoordinadaHasta);


    @Query(value = "SELECT * FROM pedido " +
            "WHERE domicilio_id = :idDomicilio " +
            "AND tipo_pedido_id = 0 " +
            "AND fecha_fin_vigencia is null", nativeQuery = true)
    Pedido getClientPedido(@Param("idDomicilio") Long idDomicilio);
}
