package com.example.aquatrack_backend.repo;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.DeudaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeudaPagoRepo extends RepoBase<DeudaPago> {

}
