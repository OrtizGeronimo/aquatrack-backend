package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByDireccionEmail(String email);

    Optional<Usuario> findByTokenPassword(String token);

    @Query(value = "SELECT * FROM usuario " +
           "WHERE direccion_email = :email " + 
           "AND fecha_fin_vigencia IS NULL",
           nativeQuery=true)
    Usuario findByEmail(String email);

    @Query(value = "SELECT id FROM usuario " +
            "WHERE estado_usuario_id = 1" +
            " AND fecha_creacion < DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY)", nativeQuery = true)
    @Modifying
    List<Long> findAllUnusedUsers();

    @Query(value = "DELETE FROM rol_usuario " +
            "WHERE usuario_id = :usuarioId", nativeQuery = true)
    @Modifying
    void deleteUserRoles(@Param("usuarioId")Long usuarioId);
}
