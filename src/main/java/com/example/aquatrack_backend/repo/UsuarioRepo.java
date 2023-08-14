package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByDireccionEmail(String email);
}
