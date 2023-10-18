package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepo extends RepoBase<Empresa> {

    Optional<Empresa> findByEmail(String email);
}
