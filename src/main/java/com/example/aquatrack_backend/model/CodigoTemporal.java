package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CodigoTemporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @DateTimeFormat(pattern = "dd-MM-YYYY HH-mm-ss")
    private LocalDateTime fechaExpiracion;

    @ManyToOne
    @JoinColumn(name = "idEmpresa")
    private Empresa empresa;

}