package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.dto.EmpresaDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.EmpresaRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Data
@Service
public class EmpresaValidator {

    @Autowired
    private EmpresaRepo empresaRepo;

    private UbicacionHelper ubicacionHelper = new UbicacionHelper();

    public void validateEmpresa(EmpresaDTO empresa, Cobertura cobertura) throws EntidadNoValidaException {
        HashMap<String, String> errors = new HashMap<>();

        if (empresaRepo.findByEmail(empresa.getMailEmpresa()).isPresent()){
            errors.put("email", "Ya existe una empresa con el email ingresado");
        }

        UbicacionDTO ubicacion = new UbicacionDTO();
        ubicacion.setLatitud(empresa.getUbicacion().getLatitud());
        ubicacion.setLongitud(empresa.getUbicacion().getLongitud());

        if (!ubicacionHelper.estaContenida(ubicacion, cobertura)){
            errors.put("root","La ubicación ingresada no se encuentra dentro de la cobertura");
        }

        if (!errors.isEmpty()){
            throw new EntidadNoValidaException(errors);
        }

    }
}
