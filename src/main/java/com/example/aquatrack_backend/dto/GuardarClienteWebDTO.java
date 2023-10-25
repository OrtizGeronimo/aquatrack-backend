package com.example.aquatrack_backend.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
public class GuardarClienteWebDTO {
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
    @NotBlank
    private String calle;
    private Integer numero;
    private String pisoDepartamento;
    private String observaciones;
    private double latitud;
    private double longitud;
    private String localidad;
    private List<PedidoProductoDTO> pedidoProductos;
}
