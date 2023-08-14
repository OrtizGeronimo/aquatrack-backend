package com.example.aquatrack_backend.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

@Getter
@Setter
@NoArgsConstructor
@Entity
@TableGenerator(name = "client_gen", table = "sequence_generator", pkColumnName = "sequence_name", valueColumnName = "next_val", allocationSize = 1)
public class Cliente extends Persona{

    private Integer dni;

    @OneToOne(mappedBy = "cliente")
    private Domicilio domicilio;
}
