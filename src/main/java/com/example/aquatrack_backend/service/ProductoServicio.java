package com.example.aquatrack_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.dto.ProductoDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Empleado;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.model.Precio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import com.example.aquatrack_backend.repo.RepoBase;

@Service
public class ProductoServicio extends ServicioBaseImpl<Producto> {

  @Autowired
  ProductoRepo productoRepo;
  @Autowired
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
      return productoRepo.getProductosActivos(id, nombre, mostrarInactivos, paging)
      .map(producto -> {
          ProductoDTO productoDTO = new ModelMapper().map(producto, ProductoDTO.class);
          for (Precio precio : producto.getPrecios()) {
              if (precio.getFechaFinVigencia() == null) {
                  productoDTO.setPrecio(precio.getPrecio());
                  break; 
              }
          }
          return productoDTO;
      });
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
        Precio precioNuevo = new Precio();
        productoNuevo.setNombre(producto.getNombre());
        productoNuevo.setDescripcion(producto.getDescripcion());
        precioNuevo.setPrecio(producto.getPrecio());
        precioNuevo.setProducto(productoNuevo);
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        productoNuevo.setEmpresa(empresa);
        productoRepo.save(productoNuevo);
        precioRepo.save(precioNuevo);
        ProductoDTO productoDTO = new ModelMapper().map(productoNuevo, ProductoDTO.class);
        productoDTO.setPrecio(precioNuevo.getPrecio());
        return productoDTO;
    }

    @Transactional
    public ProductoDTO updateProducto(Long id, GuardarProductoDTO producto) throws RecordNotFoundException {
        System.out.println("DTO ---------------------------->" + producto);
        ProductoDTO productoDTO;
        Producto productoModificado = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
        productoModificado.setNombre(producto.getNombre());
        productoModificado.setDescripcion(producto.getDescripcion());
        Precio precioActual = precioRepo.getPrecioActivo(productoModificado.getId());
        productoDTO = new ModelMapper().map(productoModificado, ProductoDTO.class);
        if(producto.getPrecio() != precioActual.getPrecio()){
          Precio precioNuevo = new Precio();
          precioNuevo.setPrecio(producto.getPrecio());
          precioNuevo.setProducto(productoModificado);
          precioActual.setFechaFinVigencia(LocalDateTime.now());
          precioRepo.save(precioActual);
          precioRepo.save(precioNuevo);
          productoDTO.setPrecio(precioNuevo.getPrecio());
        } else {
          productoDTO.setPrecio(precioActual.getPrecio());
        }
        productoRepo.save(productoModificado);
        return productoDTO;
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