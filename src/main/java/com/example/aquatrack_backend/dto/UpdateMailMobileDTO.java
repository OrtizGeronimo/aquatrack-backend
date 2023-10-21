package com.example.aquatrack_backend.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateMailMobileDTO {
  @NotBlank
  private String direccionEmail;
}
