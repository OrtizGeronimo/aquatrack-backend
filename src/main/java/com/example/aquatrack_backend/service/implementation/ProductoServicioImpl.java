package com.example.aquatrack_backend.service.implementation;

import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import com.example.aquatrack_backend.service.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicioImpl implements ProductoServicio {

    @Autowired
    ProductoRepo productoRepo;
    PrecioRepo precioRepo;
}
