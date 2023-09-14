package com.example.aquatrack_backend.helpers;

import com.example.aquatrack_backend.dto.GuardarRolDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@Data
@NoArgsConstructor
public class ValidationHelper<E>{

    public boolean hasValidationErrors(E entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<E>> violations = validator.validate(entity);
        return !violations.isEmpty();
    }

    public Map<String, List<String>> getValidationErrors(E entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<E>> violations = validator.validate(entity);

        Map<String, List<String>> errors = new HashMap<>();
        for (ConstraintViolation<E> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();

            List<String> propertyErrors = errors.getOrDefault(propertyPath, new ArrayList<>());

            propertyErrors.add(errorMessage);

            errors.put(propertyPath, propertyErrors);
        }
        return errors;
    }
}
