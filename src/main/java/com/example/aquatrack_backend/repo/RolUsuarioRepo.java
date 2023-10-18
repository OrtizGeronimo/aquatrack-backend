package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolUsuarioRepo extends RepoBase<RolUsuario> {
    @Query(value = "SELECT count(*) " +
            "FROM (" +
            "SELECT COUNT(*) as total_roles " +
            "FROM rol_usuario " +
            "WHERE usuario_id IN (SELECT usuario_id FROM rol_usuario WHERE rol_id = :rol_id) " +
            "GROUP BY usuario_id " +
            "HAVING total_roles = 1 " +
            ") as usuariosConUnicoRol",
            nativeQuery = true)
    Long usersWithRoleAsOnlyRole(@Param("rol_id") Long rol_id);
}
