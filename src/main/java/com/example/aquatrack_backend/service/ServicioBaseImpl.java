package com.example.aquatrack_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.RepoBase;

public abstract class ServicioBaseImpl<E> implements ServicioBase<E> {
  protected RepoBase<E> repoBase;

  protected ServicioBaseImpl(RepoBase<E> repoBase) {
    this.repoBase = repoBase;
  }

  @Override
  @Transactional
  public List<E> findAll() throws Exception {
    try {
      List<E> entidades = repoBase.findAll();
      return entidades;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  @Transactional
  public Page<E> findAll(Pageable pageable) throws Exception {
    try {
      Page<E> entidades = repoBase.findAll(pageable);
      return entidades;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  @Transactional
  public E findById(Long id) throws Exception {
    try {
      Optional<E> entidadOpcional = repoBase.findById(id);
      return entidadOpcional.get();
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  @Transactional
  public E save(E entidad) throws Exception {
    try {
      entidad = repoBase.save(entidad);
      return entidad;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  @Transactional
  public E update(Long id, E entidad) throws Exception {
    try {
      Optional<E> entidadOpcional = repoBase.findById(id);
      E entidadActualizada = entidadOpcional.get();
      entidadActualizada = repoBase.save(entidad);
      return entidadActualizada;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  @Override
  @Transactional
  public boolean delete(Long id) throws Exception {
    try {
      if (repoBase.existsById(id)) {
        repoBase.deleteById(id);
        return true;
      } else {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  protected Usuario getUsuarioFromContext() {
    return ((SecurityUser) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal())
        .getUsuario();
  }
}
