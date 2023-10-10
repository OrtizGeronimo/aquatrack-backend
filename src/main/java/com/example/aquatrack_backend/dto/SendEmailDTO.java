package com.example.aquatrack_backend.dto;

import java.sql.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendEmailDTO {
    
    private String email;

    private String tokenPassword;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

}
