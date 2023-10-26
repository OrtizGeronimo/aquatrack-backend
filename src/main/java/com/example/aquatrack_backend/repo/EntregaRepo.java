package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Entrega;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregaRepo extends RepoBase<Entrega> {

    @Query(value = "SELECT * FROM entrega e " +
            "WHERE e.reparto_id = :idReparto", nativeQuery = true)
    List<Entrega> findAllByReparto(@Param("idReparto") Long idReparto);

    @Query(value = "SELECT e.* FROM entrega e JOIN reparto r ON r.id = e.reparto_id WHERE e.reparto_id = :id_reparto AND e.estado_entrega_id = 2 ORDER BY e.orden_visita ASC LIMIT 1", nativeQuery = true)
    List<Entrega> findProximaEntrega(@Param("id_reparto") Long idReparto);
}