package com.example.aquatrack_backend.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rol {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
  private LocalDateTime fechaCreacion = LocalDateTime.now();
  @DateTimeFormat(pattern = "dd-MM-YYYY' 'HH:mm:ss")
  private LocalDateTime fechaFinVigencia;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rol")
  private List<PermisoRol> permisoRoles;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rol")
  private List<RolUsuario> rolUsuario;

  @ManyToOne()
  private Empresa empresa;
}
