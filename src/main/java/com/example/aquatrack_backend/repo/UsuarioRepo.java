package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
