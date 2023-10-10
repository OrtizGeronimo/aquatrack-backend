package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Reparto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartoRepo extends RepoBase<Reparto> {

    @Query(value = "SELECT * FROM reparto r JOIN estado_reparto er ON r.estado_reparto_id = er.id JOIN ruta ru ON ru.id = r.ruta_id LEFT JOIN empleado e ON e.id = r.repartidor_id " +
            " WHERE " +
            "AND (:idEstado IS NULL OR er.id = :idEstado) " +
            "AND (:idRuta IS NULL OR ru.id = :idRuta) " +
            "AND (:idRepartidor IS NULL OR e.id = :idRepartidor )" +
            "ORDER BY er.id, ru.nombre "
            ,nativeQuery = true)
    Page<Reparto> search(Long idRuta, Long idRepartidor, Long idEstado, Pageable pageable);
}
