package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.repo.PrecioRepo;
import com.example.aquatrack_backend.repo.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoServicioImpl implements ServicioBase {

    @Autowired
    ProductoRepo productoRepo;
    PrecioRepo precioRepo;
}
