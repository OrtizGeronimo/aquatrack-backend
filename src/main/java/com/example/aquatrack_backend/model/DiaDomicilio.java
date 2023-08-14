package com.example.aquatrack_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DiaDomicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @ManyToOne
    @JoinColumn(name = "idDiaRuta")
    private DiaRuta diaRuta;

    @ManyToOne
    @JoinColumn(name = "idDomicilio")
    private Domicilio domicilio;

}
