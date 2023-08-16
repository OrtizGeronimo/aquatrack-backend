package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.model.Cliente;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Producto;
import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import com.example.aquatrack_backend.repo.RepoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicioImpl extends ServicioBaseImpl<Producto> implements ServicioBase<Producto>{

    @Autowired
    ProductoRepo productoRepo;
    PrecioRepo precioRepo;

    public ProductoServicioImpl(RepoBase<Producto> repoBase) {
        super(repoBase);
    }
}
