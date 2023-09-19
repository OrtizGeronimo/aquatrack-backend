package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.CodigoTemporal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodigoTemporalRepo extends RepoBase<CodigoTemporal> {

    @Query(value = "SELECT id FROM empresa e INNER JOIN (" +
            "SELECT empresa_id as idE from codigo_temporal WHERE codigo = :codigo AND fecha_expiracion > NOW()" +
            ") ct ON e.id = ct.idE", nativeQuery = true)
    Long findEmpresaByCode(@Param("codigo") String codigo);
}