package com.example.aquatrack_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
