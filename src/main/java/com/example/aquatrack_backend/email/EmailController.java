package com.example.aquatrack_backend.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aquatrack_backend.dto.SendEmailDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UsuarioYaValidadoException;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;
    
  @PostMapping(value = "/sendPasswordEmail")
    public ResponseEntity<?> sendPasswordEmail(@RequestBody SendEmailDTO dto) throws RecordNotFoundException{
    return ResponseEntity.ok().body(emailService.sendPasswordChangeEmail(dto));
  }

  @PostMapping("/sendEmailConfirm")
    public ResponseEntity<?> sendEmailConfirm() throws RecordNotFoundException, UsuarioYaValidadoException, MailException {
      emailService.sendConfirmEmail();
      return ResponseEntity.ok().build();
  }
}
