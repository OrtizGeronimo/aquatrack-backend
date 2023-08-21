package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Precio;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServicio extends ServicioBase{

    @Autowired
    ProductoRepo productoRepo;
    PrecioRepo precioRepo;

    public List<Precio> getPrecios(Long id) throws Exception{
        try{
            Optional<Producto> producto = productoRepo.findById(id);
            List<Precio> precios = producto.get().getPrecios();
            return precios;
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
}
