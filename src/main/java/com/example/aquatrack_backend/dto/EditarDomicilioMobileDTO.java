package com.example.aquatrack_backend.dto;

import lombok.Data;

@Data
public class EditarDomicilioMobileDTO {
  private String calle;
  private Integer numero;
  private String observaciones;
  private String pisoDepto;
  private String localidad;
  private double latitud;
  private double longitud;
}
