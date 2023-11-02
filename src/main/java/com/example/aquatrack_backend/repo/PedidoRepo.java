package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepo extends RepoBase<Pedido> {

    @Query(value = "SELECT * FROM pedido p " +
            "JOIN domicilio d ON p.domicilio_id = d.id " +
            "JOIN cliente c ON c.id = d.cliente_id " +
            "WHERE c.empresa_id = :idEmpresa AND " +
            "(:mostrar_inactivos = true OR p.fecha_fin_vigencia IS NULL) AND " +
            "(:nombreCliente IS NULL OR :nombreCliente LIKE CONCAT('%', c.nombre, ' ', c.apellido, '%')) AND " +
            "(:estadoPedido IS NULL OR :estadoPedido = p.estado_pedido_id) AND " +
            "(:tipoPedido IS NULL OR :tipoPedido = p.tipo_pedido_id) AND " +
            "(:fechaCoordinadaDesde IS NULL OR :fechaCoordinadaDesde >= p.fecha_coordinada_entrega) AND " +
            "(:fechaCoordinadaHasta IS NULL OR :fechaCoordinadaHasta <= p.fecha_coordinada_entrega) " +
            "ORDER BY p.tipo_pedido_id DESC, p.fecha_coordinada_entrega DESC "
            , countQuery = "SELECT COUNT(id) FROM pedidos"
            , nativeQuery = true
    )
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
            "AND tipo_pedido_id = 1 " +
            "AND fecha_fin_vigencia is null LIMIT 1", nativeQuery = true)
    Optional<Pedido> getClientPedido(@Param("idDomicilio") Long idDomicilio);

    @Query(value = "SELECT * from pedido p WHERE p.fecha_fin_vigencia IS NULL AND p.domicilio_id = :idDomicilio AND (:estadoPedido IS NULL OR p.estado_pedido_id = :estadoPedido) AND (:tipoPedido IS NULL OR p.tipo_pedido_id = :tipoPedido) AND (:fechaCoordinadaEntrega IS NULL OR p.fecha_coordinada_entrega = :fechaCoordinadaEntrega) ORDER BY p.tipo_pedido_id DESC, p.fecha_coordinada_entrega DESC", nativeQuery = true)
    List<Pedido> pedidosCliente(@Param("idDomicilio") Long idDomicilio, @Param("estadoPedido") Long estadoPedido, @Param("tipoPedido") Long tipoPedido, @Param("fechaCoordinadaEntrega") LocalDate fechaCoordinadaEntrega);
}
