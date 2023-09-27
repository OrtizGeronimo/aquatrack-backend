package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardarClienteDTO {
    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @NotNull
    private Integer dni;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "El número de teléfono debe estar formado por 10 caracteres numéricos")
    private String numTelefono;
    @NotNull
    private Long empresaId;
    @NotNull
    private Long usuarioId;
}
