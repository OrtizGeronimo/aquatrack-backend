package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Cobertura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoberturaRepo extends RepoBase<Cobertura> {
}
