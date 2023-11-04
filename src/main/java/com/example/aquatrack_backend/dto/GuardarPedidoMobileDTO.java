package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardarPedidoMobileDTO {
    @NotNull
    private LocalDate fechaCoordinadaEntrega;
    @NotEmpty
    @Valid
    private List<PedidoProductoDTO> pedidoProductos;
}