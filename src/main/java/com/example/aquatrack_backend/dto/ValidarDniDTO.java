package com.example.aquatrack_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidarDniDTO {
    private Integer dni;
    private Long empresaId;
    private Long usuarioId;
}
