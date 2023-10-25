package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.dto.GuardarClienteWebDTO;
import com.example.aquatrack_backend.dto.UbicacionDTO;
import com.example.aquatrack_backend.exception.ClienteNoCubiertoApp;
import com.example.aquatrack_backend.exception.ClienteNoValidoException;
import com.example.aquatrack_backend.exception.EntidadNoValidaException;
import com.example.aquatrack_backend.helpers.UbicacionHelper;
import com.example.aquatrack_backend.model.Cobertura;
import com.example.aquatrack_backend.model.Empresa;
import com.example.aquatrack_backend.repo.ClienteRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ClientValidator {
    @Autowired
    private ClienteRepo clienteRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private UserValidator userValidator;

    private UbicacionHelper ubicacionHelper = new UbicacionHelper();

    public void validateAppClient(UbicacionDTO ubicacion, Cobertura cobertura) throws ClienteNoCubiertoApp {

        HashMap<String, String> errors = new HashMap<>();

        if (!validateIsContained(ubicacion, cobertura)) {
            errors.put("root", "El cliente ingresado no está contenido en la cobertura de la empresa.");
        }

        if (!errors.isEmpty()) {
            throw new ClienteNoCubiertoApp("El cliente no se encuentra contenido en la cobertura de la empresa");
        }
    }

    public void validateWebClient(GuardarClienteWebDTO clienteDTO, Empresa empresa) throws ClienteNoValidoException {

        HashMap<String, String> errors = new HashMap<>();

        if (!validateUniqueDni(clienteDTO.getDni(), empresa.getId())) {
            errors.put("dni", "El dni ingresado ya se encuentra vinculado a un cliente de la empresa");
        }

        UbicacionDTO ubicacionDTO = UbicacionDTO.builder().latitud(clienteDTO.getLatitud()).longitud(clienteDTO.getLongitud()).build();
        if (!validateIsContained(ubicacionDTO, empresa.getCobertura())) {
            errors.put("root", "El cliente ingresado no está contenido en la cobertura de la empresa.");
        }

        if (!errors.isEmpty()) {
            throw new ClienteNoValidoException(errors);
        }
    }

    public void validateWebClientUpdate(GuardarClienteWebDTO clienteDTO, Empresa empresa) throws EntidadNoValidaException {

        HashMap<String, String> errors = new HashMap<>();

        if (!validateUniqueDniUpdate(clienteDTO.getDni(), empresa.getId(), clienteDTO.getId())) {
            errors.put("dni", "El dni ingresado ya se encuentra vinculado a un cliente de la empresa");
        }

        if (!errors.isEmpty()) {
            throw new EntidadNoValidaException(errors);
        }
    }

    private boolean validateUniqueDni(Integer dni, Long idE) {
        return clienteRepo.validateUniqueDni(dni, idE) <= 0;
    }

    private boolean validateIsContained(UbicacionDTO ubiCliente, Cobertura cobertura) {

        return ubicacionHelper.estaContenida(ubiCliente, cobertura);
    }

    private boolean validateUniqueDniUpdate(Integer dni, Long idE, Long idC) {
        List<Long> result = clienteRepo.validateDniUpdate(dni, idE);

        if(!result.isEmpty() && !result.contains(idC)) {
            return false;
        }

        return true;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void cleanUnusedClient(){
        List<Long> idsClients = clienteRepo.findAllUnusedClients();
        for (Long client: idsClients) {
            clienteRepo.deleteClientDomicily(client);
            clienteRepo.deleteById(client);
        }
        userValidator.cleanUnusedClientUsers();
    }
}
