package com.example.aquatrack_backend.email;

import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.SendEmailDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.exception.UsuarioYaValidadoException;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    UsuarioRepo usuarioRepo;

    private JavaMailSender mailSender;

    @Value("${mail.urlFront}")
    private String urlEmail;

    @Value("${mail.urlConfirm}")
    private String urlConfirm;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordChangeEmail(SendEmailDTO dto) throws RecordNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByDireccionEmail(dto.getEmail());
        if (usuarioOpt.isEmpty()) {
            throw new RecordNotFoundException("No existe un usuario con esa dirección de correo electŕonico.");
        }
        Usuario usuario = usuarioOpt.get();
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        usuario.setTokenPassword(token);
        dto.setTokenPassword(token);
        String from = "aquatrack.help@gmail.com";
        String subject = "Cambio de contraseña - Aquatrack";
        String body = usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido() + ", usted ha solicitado un cambio de contraseña. Por favor, ingrese en el siguiente enlace para proseguir: " + urlEmail + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(dto.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        usuarioRepo.save(usuario);
    }


    public void sendConfirmEmail() throws RecordNotFoundException, UsuarioYaValidadoException, MailException {
        Usuario usuario = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();
        if (usuario.getValidado()) {
            throw new UsuarioYaValidadoException("El usuario ya se encuentra validado.");
        }

        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();

        usuario.setTokenEmail(token);
        String from = "aquatrack.help@gmail.com";
        String subject = "Activación de cuenta - Aquatrack";
        String body = usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido() + ", para activar su cuenta de AquaTrack por favor haga click en el siguiente enlace: " + urlConfirm + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(usuario.getDireccionEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        usuarioRepo.save(usuario);
    }
}

