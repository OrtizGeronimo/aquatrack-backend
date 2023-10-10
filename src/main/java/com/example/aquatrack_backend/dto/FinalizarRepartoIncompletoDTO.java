package com.example.aquatrack_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class FinalizarRepartoIncompletoDTO {

    private Long idReparto;
    @NotNull
    @NotEmpty
    private String observaciones;
}
