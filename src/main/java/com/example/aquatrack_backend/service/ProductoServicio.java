package com.example.aquatrack_backend.service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

  public Page<ProductoDTO> getProductosActivos(int page, int size, String nombre, boolean mostrarInactivos, int precio1, int precio2) {
      Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Long id = empresa.getId();
      Pageable paging = PageRequest.of(page, size);
      // Page<Producto> productos = productoRepo.getProductosActivos(id, nombre, mostrarInactivos, paging);
      return productoRepo.getProductosActivos(id, nombre, mostrarInactivos, precio1, precio2, paging)
      .map(producto -> {
          ProductoDTO productoDTO = new ModelMapper().map(producto, ProductoDTO.class);
          String ruta = "C://aquatrack/imagenes"; // Change to your image storage path
          String imagenFileName = producto.getImagen();
          if (imagenFileName != null) {
              productoDTO.setImagen(ruta + "/" + imagenFileName);
          }
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
  public String uploadImage(MultipartFile imagen, String codigo){
    try{
        String ruta = "C://aquatrack/imagenes";
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Producto producto = productoRepo.findByCode(codigo, empresa.getId());
        int index = imagen.getOriginalFilename().indexOf(".");
        String extension = "." + imagen.getOriginalFilename().substring(index+1);
        String nombre = Calendar.getInstance().getTimeInMillis() + extension;
        Path rutaAbsoluta = producto.getImagen() != null ?  Paths.get(ruta + "//"+producto.getImagen()) : 
                                                            Paths.get(ruta + "//"+nombre);
        Files.write(rutaAbsoluta, imagen.getBytes());
        producto.setImagen(nombre);
        productoRepo.save(producto);
        return "Exito";
    } catch (Exception e){
        return e.getMessage();
    }
  }

  @Transactional
    public ProductoDTO createProducto(GuardarProductoDTO producto) {
        Producto productoNuevo = new Producto();
        Precio precioNuevo = new Precio();
        productoNuevo.setNombre(producto.getNombre());
        productoNuevo.setDescripcion(producto.getDescripcion());
        productoNuevo.setCodigo(producto.getCodigo());
        // pruductoNuevo.setImagen(producto.getImagen());
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