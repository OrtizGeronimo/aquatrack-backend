package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Permiso;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepo extends RepoBase<Permiso> {
  
  @Query(value = "SELECT DISTINCT p.descripcion " +
                 "FROM permiso p JOIN permiso_rol pr ON p.id = pr.permiso_id " +
                 "INNER JOIN rol r ON pr.rol_id = r.id " +
                 "INNER JOIN rol_usuario ur ON ur.rol_id = r.id WHERE ur.usuario_id = :usuario_id", nativeQuery = true)
  List<String> findPermisosByUsuarioId(@Param("usuario_id") Long id);
}
