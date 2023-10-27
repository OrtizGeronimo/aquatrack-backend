package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.dto.PedidoProductoProjection;
import com.example.aquatrack_backend.model.PedidoProducto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoProductoRepo extends RepoBase<PedidoProducto> {

    @Query(value = "SELECT pr.id as id, pr.nombre as nombre, SUM(pre.precio) as precio, SUM(pp.cantidad) as cantidad FROM entrega_pedido ep \n" +
            "JOIN pedido p ON p.id = ep.pedido_id \n" +
            "JOIN pedido_producto pp ON p.id = pp.pedido_id\n" +
            "JOIN producto pr ON pr.id = pp.producto_id\n" +
            "JOIN precio pre ON pr.id = pre.producto_id\n" +
            "WHERE pre.fecha_fin_vigencia IS NULL AND ep.entrega_id = :idEntrega\n" +
            "GROUP BY pr.id, pr.nombre", nativeQuery = true)
    List<PedidoProductoProjection> getAllPedidoProductos(Long idEntrega);
}
