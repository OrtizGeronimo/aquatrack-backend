package com.example.aquatrack_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepoBase<E> extends JpaRepository<E, Long> {
}
