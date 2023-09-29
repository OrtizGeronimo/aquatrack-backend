package com.example.aquatrack_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ValidarDniDTO {
    @NotNull
    private Integer dni;
    private Long empresaId;
    private Long usuarioId;
}
