package com.example.aquatrack_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServicioBase<E> {
    public List<E> findAll() throws Exception;

    public Page<E> findAll(Pageable pageable) throws Exception;

    public E findById(Long id) throws Exception;

    public E save(E entity) throws Exception;

    public E update(Long id, E entity) throws Exception;

    public boolean delete(Long id) throws Exception;
}
