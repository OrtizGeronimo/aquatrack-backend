package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardarProductoDTO {

    @NotBlank()
    private String nombre;

    private String descripcion;

    @NotNull()
    private BigDecimal precio;

    private String imagen;

    private Boolean retornable;

    @NotBlank
    private String codigo;

    @Min(value = 1, message = "La cantidad máxima de un producto solicitable por un cliente debe ser al menos una unidad.")
    private Integer maximo;
}