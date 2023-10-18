package com.example.aquatrack_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreEstadoUsuario;

    @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
    private LocalDateTime fechaFinVigencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "estadoUsuario")
    private List<Usuario> usuarios;
}
