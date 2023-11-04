package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.dto.GuardarProductoDTO;
import com.example.aquatrack_backend.exception.ProductoNoValidoException;
import com.example.aquatrack_backend.repo.ProductoRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ProductoValidator {

    @Autowired
    ProductoRepo productoRepo;

    @Transactional
    public void validateCrearProducto(GuardarProductoDTO producto, Long idE) throws ProductoNoValidoException {

        HashMap<String, String> errors = new HashMap<>();

        if(!validateCodigoUnico(producto.getCodigo(), idE)){
            errors.put("codigo", "Ya existe un producto con el código ingresado.");
        }

        if(!errors.isEmpty()){
            throw new ProductoNoValidoException(errors);
        }
    }

    private boolean validateCodigoUnico(String codigo, Long idE){
        return productoRepo.validateCodigoUnico(codigo, idE) <= 0;
    }
}
