package com.example.aquatrack_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.PermisoRol;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Precio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.model.Rol;
import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import com.example.aquatrack_backend.dto.CurrentUserDTO;
import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.dto.GuardarRolDTO;
import com.example.aquatrack_backend.dto.PermisoDTO;
import com.example.aquatrack_backend.dto.ProductoDTO;
import com.example.aquatrack_backend.dto.RolDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UserWithOneRolePresentException;

@Service
public class ProductoServicio extends ServicioBaseImpl<Producto> {

  @Autowired
  ProductoRepo productoRepo;
  PrecioRepo precioRepo;

  public ProductoServicio(RepoBase<Producto> repoBase) {
    super(repoBase);
  }

  public List<Precio> getPrecios(Long id) throws Exception {
    try {
      Optional<Producto> producto = productoRepo.findById(id);
      List<Precio> precios = producto.get().getPrecios();
      return precios;
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }

  public Page<ProductoDTO> getProductosActivos(int page, int size, String nombre, boolean mostrarInactivos) {
      Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Long id = empresa.getId();
      Pageable paging = PageRequest.of(page, size);
      // Page<Producto> productos = productoRepo.getProductosActivos(id, nombre, mostrarInactivos, paging);
      return productoRepo.getProductosActivos(id, nombre, mostrarInactivos, paging).map(rol -> new ModelMapper().map(rol, ProductoDTO.class));
      // List<ProductoDTO> productDTOs = new ArrayList<>();
      //   for (Producto producto : productos) {
      //       ProductoDTO productDTO = new ProductoDTO();
      //       productDTO.setId((Long) producto.getId());
      //       productDTO.setNombre((String) producto.getNombre());
      //       productDTO.setDescripcion((String) producto.getDescripcion());
      //       productDTO.setFechaFinVigencia((LocalDateTime) producto.getFechaFinVigencia());
      //       productDTOs.add(productDTO);
      //   }
      //   return productDTOs;
  }

  @Transactional
    public ProductoDTO createProducto(GuardarProductoDTO producto) {
        Producto productoNuevo = new Producto();
        productoNuevo.setNombre(producto.getNombre());
        productoNuevo.setDescripcion(producto.getDescripcion());
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        productoNuevo.setEmpresa(empresa);
        productoRepo.save(productoNuevo);
        return new ModelMapper().map(productoNuevo, ProductoDTO.class);
    }

    @Transactional
    public ProductoDTO updateProducto(Long id, GuardarProductoDTO producto) throws RecordNotFoundException {
        Producto productoModificado = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
        productoModificado.setNombre(producto.getNombre());
        productoModificado.setDescripcion(producto.getDescripcion());
        productoRepo.save(productoModificado);
        return new ModelMapper().map(productoModificado, ProductoDTO.class);
    }

    @Transactional
    public void disable(Long id) throws Exception {
        Producto productoDeshabilitado = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
        if(productoDeshabilitado.getFechaFinVigencia() != null){
          throw new Exception("El producto seleccionado ya se encuentra dado de baja");
        }
        productoDeshabilitado.setFechaFinVigencia(LocalDateTime.now());
        productoRepo.save(productoDeshabilitado);
    }

    @Transactional
    public ProductoDTO enable(Long id) throws RecordNotFoundException {
        Producto productoRehabilitado = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
        productoRehabilitado.setFechaFinVigencia(null);
        productoRepo.save(productoRehabilitado);
        return new ModelMapper().map(productoRehabilitado, ProductoDTO.class);
    }
}