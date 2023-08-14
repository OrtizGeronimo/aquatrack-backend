package com.example.aquatrack_backend.model;


import javax.persistence.*;

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
    private DiaRuta diaRuta;

    @ManyToOne
    private Domicilio domicilio;

}
