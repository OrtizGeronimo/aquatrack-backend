package com.example.aquatrack_backend.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Rol;

@Repository
public interface RolRepo extends RepoBase<Rol> {
    @Query(value = "SELECT * FROM rol WHERE empresa_id = :empresa_id " +
            "AND (:nombre IS NULL OR nombre LIKE %:nombre%) " +
            "AND (:mostrar_inactivos = true OR fecha_fin_vigencia IS NULL)",
            nativeQuery = true)
    Page<Rol> findAllByEmpresa(@Param("empresa_id") Long empresaId, @Param("nombre") String nombre, @Param("mostrar_inactivos") boolean mostrarInactivos, Pageable pageable);

    @Query(value = "SELECT * FROM rol WHERE nombre LIKE %:nombre% " +
                    "AND empresa_id = :empresaId", nativeQuery = true)
    Rol findByName(String nombre, Long empresaId);
}
