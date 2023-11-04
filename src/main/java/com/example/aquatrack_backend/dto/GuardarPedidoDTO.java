package com.example.aquatrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuardarPedidoDTO {
    @NotNull
    private LocalDate fechaCoordinadaEntrega;
    @NotNull
    private Long idDomicilio;
    @NotEmpty
    @Valid
    private List<PedidoProductoDTO> pedidoProductos;
    //private Long idRuta; //Caso de pedido extraordinario
}
