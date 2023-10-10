package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.dto.GuardarClienteWebDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientValidator {
    @Autowired
    private ClienteRepo clienteRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    private UbicacionHelper ubicacionHelper = new UbicacionHelper();

    public void validateAppClient(UbicacionDTO ubicacion, Cobertura cobertura) throws ClienteNoValidoException {

        HashMap<String, String> errors = new HashMap<>();

        if(!validateIsContained(ubicacion, cobertura)){
            errors.put("root", "El cliente ingresado no está contenido en la cobertura de la empresa.");
        }

        if(!errors.isEmpty()){
            throw new ClienteNoValidoException(errors);
        }
    }

    public void validateWebClient(GuardarClienteWebDTO clienteDTO, Empresa empresa) throws ClienteNoValidoException {

        HashMap<String, String> errors = new HashMap<>();

        if(!validateUniqueDni(clienteDTO.getDni(), empresa.getId())){
            errors.put("dni", "El dni ingresado ya se encuentra vinculado a un cliente de la empresa");
        }

        UbicacionDTO ubicacionDTO = UbicacionDTO.builder().latitud(clienteDTO.getLatitud()).longitud(clienteDTO.getLongitud()).build();
        if(!validateIsContained(ubicacionDTO, empresa.getCobertura())){
            errors.put("root", "El cliente ingresado no está contenido en la cobertura de la empresa.");
        }

        if(!errors.isEmpty()){
            throw new ClienteNoValidoException(errors);
        }
    }

    private boolean validateUniqueDni(Integer dni, Long idE){
        if(clienteRepo.validateUniqueDni(dni, idE) > 0){
            return false;
        }
        return true;
    }

    private boolean validateIsContained(UbicacionDTO ubiCliente, Cobertura cobertura){
        boolean isContained = ubicacionHelper.estaContenida(ubiCliente, cobertura);
        if(!isContained){
            return false;
        }
        return true;
    }
}
