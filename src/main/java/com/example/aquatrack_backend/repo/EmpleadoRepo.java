package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Rol;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepo extends RepoBase<Empleado> {

    // @Query(value = "SELECT e.id, e.nombre, e.apellido, e.legajo, e.fecha_fin_vigencia, e.fecha_ingreso, e.fecha_creacion, " +
        @Query(value = "SELECT * FROM empleado e " + 
            // "t.nombre FROM empleado e  " + 
            "INNER JOIN tipo_empleado t ON t.id = e.tipo_id " + 
            "WHERE e.empresa_id = :empresaId " +
            "AND (:nombre IS NULL OR e.nombre LIKE %:nombre% OR e.apellido LIKE %:nombre%) " +
            "AND (:mostrarInactivos = true OR e.fecha_fin_vigencia IS NULL)",
            nativeQuery = true)
    Page<Empleado> findAllByEnterprise(Long empresaId, String nombre, boolean mostrarInactivos, Pageable pageable);

}
