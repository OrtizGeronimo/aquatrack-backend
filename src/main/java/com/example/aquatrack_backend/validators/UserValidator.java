package com.example.aquatrack_backend.validators;

import com.example.aquatrack_backend.exception.UserNoValidoException;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserValidator {

    @Autowired
    UsuarioRepo usuarioRepo;

    public void validateClientUser(String mail) throws UserNoValidoException {
        HashMap<String, String> errors = new HashMap<>();

        if(usuarioRepo.findByDireccionEmail(mail) != null){
            errors.put("direccionEmail", "Ya existe un usuario con la dirección de mail ingresada.");
        }

        if(!errors.isEmpty()){
            throw new UserNoValidoException(errors);
        }
    }
}
