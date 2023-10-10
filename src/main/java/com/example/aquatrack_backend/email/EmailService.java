package com.example.aquatrack_backend.email;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.aquatrack_backend.dto.SendEmailDTO;
import com.example.aquatrack_backend.exception.RecordNotFoundException;
import com.example.aquatrack_backend.model.Usuario;
import com.example.aquatrack_backend.repo.UsuarioRepo;

@Service
public class EmailService{

    @Autowired
    UsuarioRepo usuarioRepo;
	
	private JavaMailSender mailSender;

    @Value("${mail.urlFront}")
    private String urlEmail;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // public void sendEmail(String from, String to, String subject, String text) {
    //     SimpleMailMessage message = new SimpleMailMessage();
    //     message.setFrom(from);
    //     message.setTo(to);
    //     message.setSubject(subject);
    //     message.setText(text);
    //     mailSender.send(message);
    // }

    // public void sendWithAttach(String from, String to, String subject,
    //                            String text, String attachName,
    //                            InputStreamSource inputStream) throws MessagingException {
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //     helper.setFrom(from);
    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(text, true);
    //     helper.addAttachment(attachName, inputStream);
    //     mailSender.send(message);
    // }

    public SendEmailDTO sendPasswordChangeEmail( SendEmailDTO dto ) throws RecordNotFoundException{
        Optional<Usuario> usuarioOpt = usuarioRepo.findByDireccionEmail(dto.getEmail());
        if (!usuarioOpt.isPresent()) {
            throw new RecordNotFoundException("El usuario no fue encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        // String token = generateRandomCode(8);
        // String token = uuid.toString();
        usuario.setTokenPassword(token);
        dto.setTokenPassword(token);
        String from = "aquatrack.help@gmail.com";
        String subject = "Cambio de contraseña - Aquatrack";
        String body = usuario.getPersona().getNombre() + ", usted ha solicitado un cambio de contraseña. Por favor ingrese en el siguiente link para realizarlo: " + urlEmail + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(dto.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        usuarioRepo.save(usuario);
        return dto;
    }

    // public static String shortenUUID(UUID uuid) {
    //     try {
    //         // Convert UUID to bytes
    //         byte[] uuidBytes = toBytes(uuid);

    //         // Create an MD5 hash of the UUID bytes
    //         byte[] md5Bytes = MessageDigest.getInstance("MD5").digest(uuidBytes);

    //         // Convert the MD5 hash to a hexadecimal string
    //         StringBuilder sb = new StringBuilder();
    //         for (byte b : md5Bytes) {
    //             sb.append(String.format("%02x", b));
    //         }

    //         // Return the first 10 characters of the MD5 hash
    //         return sb.toString().substring(0, 8);
    //     } catch (NoSuchAlgorithmException e) {
    //         throw new RuntimeException("MD5 algorithm not available", e);
    //     }
    // }

    // private static byte[] toBytes(UUID uuid) {
    //     long msb = uuid.getMostSignificantBits();
    //     long lsb = uuid.getLeastSignificantBits();
    //     byte[] buffer = new byte[16];
    //     for (int i = 0; i < 8; i++) {
    //         buffer[i] = (byte) (msb >>> 8 * (7 - i));
    //     }
    //     for (int i = 8; i < 16; i++) {
    //         buffer[i] = (byte) (lsb >>> 8 * (7 - i));
    //     }
    //     return buffer;
    // }

    // public static String generateRandomCode(int length) {
    //     // Define the characters allowed in the code
    //     String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    //     SecureRandom random = new SecureRandom();
    //     StringBuilder code = new StringBuilder(length);

    //     for (int i = 0; i < length; i++) {
    //         int randomIndex = random.nextInt(characters.length());
    //         char randomChar = characters.charAt(randomIndex);
    //         code.append(randomChar);
    //     }

    //     return code.toString();
    // }
}

