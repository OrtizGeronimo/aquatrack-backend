package com.example.aquatrack_backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
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
import com.example.aquatrack_backend.dto.ImagenDTO;
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
public class
ProductoServicio extends ServicioBaseImpl<Producto> {

  @Autowired
  ProductoRepo productoRepo;
  @Autowired
  PrecioRepo precioRepo;
  // @Autowired
  // private ResourceLoader resourceLoader;
  // @Value("${image.upload.directory}")
  // private String uploadDirectory;

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

    public Page<ProductoDTO> getProductosActivos(int page, int size, String nombre, boolean mostrarInactivos, int precio1, int precio2) throws Exception, IOException {
      Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Long id = empresa.getId();
      Pageable paging = PageRequest.of(page, size);

      return productoRepo.getProductosActivos(id, nombre, mostrarInactivos, precio1, precio2, paging)
              .map(producto -> {
                  ProductoDTO productoDTO = new ModelMapper().map(producto, ProductoDTO.class);
                  // String imagenFileName = producto.getImagen();
                  // if (imagenFileName != null) {
                  //     try {
                  //           String relativePath = "src/main/resources/images/";
                  //           String uploadDirectory = System.getProperty("user.dir") + "/" + relativePath;
                  //           String imagePath = Paths.get(uploadDirectory, imagenFileName).toString();

                  //           // Check if the image file exists
                  //           if (Files.exists(Paths.get(imagePath))) {
                  //             // Read the image file as bytes
                  //             byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));

                  //             // Encode the image bytes to base64
                  //             String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                  //             productoDTO.setImagen(base64Image);
                  //           }
                  //     } catch (IOException e) {
                  //         // Handle the IOException or rethrow it as a more specific exception
                  //         throw new RuntimeException("Error while processing image file", e);
                  //     }
                  // }

                  for (Precio precio : producto.getPrecios()) {
                      if (precio.getFechaFinVigencia() == null) {
                          productoDTO.setPrecio(precio.getPrecio());
                          break;
                      }
                  }
                  return productoDTO;
              });
  }

  public ImagenDTO getImagen(Long id) throws RecordNotFoundException{
    ImagenDTO imagenDTO = new ImagenDTO();
    Producto producto = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
    String imagenFileName = producto.getImagen();
    // String imagen = "";
        if (imagenFileName != null) {
             try {
                      String relativePath = "src/main/resources/images/";
                      String uploadDirectory = System.getProperty("user.dir") + "/" + relativePath;
                      String imagePath = Paths.get(uploadDirectory, imagenFileName).toString();

                            // Check if the image file exists
                      if (Files.exists(Paths.get(imagePath))) {
                              // Read the image file as bytes
                          byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));

                              // Encode the image bytes to base64
                          String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                          imagenDTO.setBase64(base64Image);
                      }
                      } catch (IOException e) {
                          // Handle the IOException or rethrow it as a more specific exception
                          throw new RuntimeException("Error while processing image file", e);
                      }
                  }
        return imagenDTO;
  }

  public void uploadImage(MultipartFile imageFile, Long id) throws IOException, RecordNotFoundException {
    try {
      // Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
      Producto producto = productoRepo.findById(id).orElseThrow(() -> new RecordNotFoundException("El producto solicitado no fue encontrado"));
      // String relativePath = "src/main/resources/images/";
      int index = imageFile.getOriginalFilename().indexOf(".");
      String extension = "." + imageFile.getOriginalFilename().substring(index+1);
      // if(producto.getImagen() != null){
      //   //  String relativePath = "src/main/resources/images/"+producto.getImagen();
      //    String uploadDirectory = System.getProperty("user.dir") + "/" + relativePath;
      //    String nombre = producto.getImagen() + extension;
      //    String imagePath = Paths.get(uploadDirectory).toString();
      // } else {
      //   String relativePath =  "src/main/resources/images/";
      //   String uploadDirectory = System.getProperty("user.dir") + "/" + relativePath;
      //   String nombre = Calendar.getInstance().getTimeInMillis() + extension;
      //   String imagePath = Paths.get(uploadDirectory, nombre).toString();
      // }
      String relativePath = producto.getImagen() != null ?  "src/main/resources/images/"+producto.getImagen() :
                                                            "src/main/resources/images/";
      String uploadDirectory = System.getProperty("user.dir") + "/" + relativePath;
      Path directoryPath = Paths.get(relativePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
      }
      // int index = imageFile.getOriginalFilename().indexOf(".");
      // String extension = "." + imageFile.getOriginalFilename().substring(index+1);
      String nombre = producto.getImagen() != null ? producto.getImagen():
                                                     Calendar.getInstance().getTimeInMillis() + extension;
      String imagePath = producto.getImagen() != null ? Paths.get(uploadDirectory).toString() :
                                                        Paths.get(uploadDirectory, nombre).toString();
      File destinationFile = new File(imagePath);

      // Create parent directories if they don't exist
      destinationFile.getParentFile().mkdirs();

      // Save the image file
      imageFile.transferTo(destinationFile);
      producto.setImagen(nombre);
      productoRepo.save(producto);
  } catch (IOException e) {
      e.printStackTrace(); // Add proper logging here
    // Handle the exception or return an error response if necessary
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

    public boolean verificarCodigoExistente(String codigo)  {
        Empresa empresa = ((Empleado) getUsuarioFromContext().getPersona()).getEmpresa();
        Optional<Producto> producto = productoRepo.findByCode(codigo, empresa.getId());
        boolean flag = false;
        if (producto.isPresent()) {
          flag = true;
        }
        return flag;
    }

}