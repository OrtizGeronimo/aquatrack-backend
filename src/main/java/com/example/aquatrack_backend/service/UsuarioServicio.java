package com.example.aquatrack_backend.service;

import com.example.aquatrack_backend.config.JwtUtils;
import com.example.aquatrack_backend.config.SecurityUser;
import com.example.aquatrack_backend.dto.*;
import com.example.aquatrack_backend.exception.*;
import com.example.aquatrack_backend.model.*;
import com.example.aquatrack_backend.repo.EmpleadoRepo;
import com.example.aquatrack_backend.repo.EstadoUsuarioRepo;
import com.example.aquatrack_backend.repo.RolRepo;
import com.example.aquatrack_backend.repo.UsuarioRepo;
import com.example.aquatrack_backend.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepo usuarioRepo;
    @Autowired
    private EmpleadoRepo empleadoRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServicio(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RolRepo rolRepo;
    @Autowired
    private EstadoUsuarioRepo estadoUsuarioRepo;
    @Autowired
    private UserValidator userValidator;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public LoginResponseDTO login(String direccionEmail, String contraseña) throws UserUnauthorizedException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
        Persona persona = ((SecurityUser) authentication.getPrincipal()).getUsuario().getPersona();
        if (persona instanceof Cliente) {
            throw new UserUnauthorizedException("No puede iniciar sesión como cliente en AquaTrack Web.");
        }
        String jwt = jwtUtils.generateJwtToken(authentication);
        return LoginResponseDTO.builder().token(jwt).build();
    }

    public LoginMobileResponseDTO loginMobile(String direccionEmail, String contraseña) throws UserUnauthorizedException, EntidadNoVigenteException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(direccionEmail, contraseña));
        Persona persona = ((SecurityUser) authentication.getPrincipal()).getUsuario().getPersona();
        if (persona.getFechaFinVigencia() != null) {
            throw new EntidadNoVigenteException("El usuario fue dado de baja.");
        }

        if (persona instanceof Empleado) {
            if (!((Empleado) persona).getTipo().getNombre().equalsIgnoreCase("repartidor")) {
                throw new UserUnauthorizedException("El usuario ingresado no puede acceder a la aplicación mobile.");
            }
        }
        String jwt = jwtUtils.generateJwtToken(authentication);
        return LoginMobileResponseDTO.builder()
                .token(jwt)
                .build();
    }

    private Usuario createUser(String mail, String password, String confirmacionPassword) {
        Usuario usuario = new Usuario();
        usuario.setDireccionEmail(mail);
        usuario.setContraseña(bCryptPasswordEncoder.encode(password));
        usuario.setConfirmacionContraseña(bCryptPasswordEncoder.encode(confirmacionPassword));
        usuario.setValidado(true);
        return usuario;
    }

    @Transactional
    public RegisterResponseDTO clientRegister(RegisterRequestDTO register) throws UserNoValidoException, RecordNotFoundException {
        userValidator.validateClientUser(register.getDireccionEmail());
        Usuario usuario = createUser(register.getDireccionEmail(), register.getContraseña(), register.getConfirmacionContraseña());
        usuario.setEstadoUsuario(estadoUsuarioRepo.findByNombreEstadoUsuario("En proceso de creación").orElseThrow(() -> new RecordNotFoundException("El estado no fue encontrado.")));
        Rol rol = rolRepo.findClientRole();
        List<RolUsuario> rolUsuarios = new ArrayList<>();
        rolUsuarios.add(new RolUsuario(rol, usuario));
        usuario.setRolesUsuario(rolUsuarios);
        Usuario usuarioGuardado = usuarioRepo.save(usuario);
        return new RegisterResponseDTO(register.getIdEmpresa(), usuarioGuardado.getId());
    }

    public CurrentUserDTO getCurrentUser() throws FailedToAuthenticateUserException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();
            Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
            List<String> permisos = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            Rol rol = rolRepo.findRolWithHighestPermissions(getUsuarioFromContext().getId());
            return CurrentUserDTO.builder()
                    .password(userDetails.getPassword())
                    .nombre(empleado.getNombre() + " " + empleado.getApellido())
                    .empresa(empleado.getEmpresa().getNombre())
                    .rolPrincipal(rol.getNombre())
                    .validado(getUsuarioFromContext().getValidado())
                    .permisos(permisos)
                    .direccionEmail(getUsuarioFromContext().getDireccionEmail())
                    .build();
        } else {
            throw new FailedToAuthenticateUserException("Error de autenticación. Intente mas tarde.");
        }
    }

    public CurrentUserMobileDTO getCurrentUserMobile() throws FailedToAuthenticateUserException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Persona persona = getUsuarioFromContext().getPersona();
            return CurrentUserMobileDTO.builder()
                    .nombre(persona.getNombre() + " " + persona.getApellido())
                    .empresa(persona.getEmpresa().getNombre())
                    .tipoPersona(persona instanceof Empleado ? "repartidor" : "cliente")
                    .validado(getUsuarioFromContext().getValidado())
                    .direccionEmail(getUsuarioFromContext().getDireccionEmail())
                    .build();
        } else {
            throw new FailedToAuthenticateUserException("Error de autenticación. Intente mas tarde.");
        }
    }

    @Transactional
    public Usuario createUserClient(String email, String password, Long empresaId) {
        Usuario usuario = new Usuario();
        usuario.setDireccionEmail(email);
        usuario.setContraseña(bCryptPasswordEncoder.encode(password));
        usuario.setValidado(true);
        Rol rol = rolRepo.findClientRole();
        List<RolUsuario> rolUsuarios = new ArrayList<>();
        rolUsuarios.add(new RolUsuario(rol, usuario));
        usuario.setRolesUsuario(rolUsuarios);
        usuarioRepo.save(usuario);
        return usuario;
    }

    @Transactional
    public UpdateUserDTO updateUserProfile(UpdateUserDTO usuarioDTO) {
        Usuario usuario = getUsuarioFromContext();
        Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
        usuario.setDireccionEmail(usuarioDTO.getEmail());
        empleado.setApellido(usuarioDTO.getApellido());
        empleado.setNombre(usuarioDTO.getNombre());
        empleado.setNumTelefono(usuarioDTO.getNroTelefono());
        usuarioRepo.save(usuario);
        empleadoRepo.save(empleado);
        return usuarioDTO;
    }

    public UpdateUserDTO getUserProfile() {
        Usuario usuario = getUsuarioFromContext();
        Empleado empleado = (Empleado) getUsuarioFromContext().getPersona();
        UpdateUserDTO usuarioDTO = new UpdateUserDTO();
        usuarioDTO.setEmail(usuario.getDireccionEmail());
        usuarioDTO.setApellido(empleado.getApellido());
        usuarioDTO.setNombre(empleado.getNombre());
        usuarioDTO.setNroTelefono(empleado.getNumTelefono());
        return usuarioDTO;
    }

    @Transactional
    public void changePasswordProfile(ChangePasswordDTO dto) throws EntidadNoValidaException {
        Usuario usuario = getUsuarioFromContext();
        boolean isPasswordCorrect = verifyPassword(usuario.getContraseña(), dto.getFormerPassword());
        if (isPasswordCorrect) {
            usuario.setContraseña(bCryptPasswordEncoder.encode(dto.getPassword()));
            usuarioRepo.save(usuario);
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("formerPassword", "La contraseña actual es incorrecta.");
            throw new EntidadNoValidaException(errors);
        }
    }

    private Usuario getUsuarioFromContext() {
        return ((SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getUsuario();
    }

    public boolean verifyPassword(String storedHashedPassword, String userInputPassword) {
        return bCryptPasswordEncoder.matches(userInputPassword, storedHashedPassword);
    }

    public void updatePassword(ChangePasswordLoginDTO dto) throws RecordNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByTokenPassword(dto.getTokenPassword());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String password = passwordEncoder.encode(dto.getPassword());
            usuario.setContraseña(password);
            usuario.setTokenPassword(null);
            usuarioRepo.save(usuario);
        } else {
            throw new RecordNotFoundException("El enlace para cambiar la contraseña del usuario no es válido.");
        }
    }

    public void confirmAccount(String token) throws RecordNotFoundException {
        Usuario usuario = usuarioRepo.findByTokenEmail(token).orElseThrow(() -> new RecordNotFoundException("No se puede verificar el usuario con este enlace."));

        usuario.setValidado(true);

        usuarioRepo.save(usuario);
    }

    public void updateMailMobile(UpdateMailMobileDTO mail) throws UserNoValidoException {
        Usuario usuario = getUsuarioFromContext();
        userValidator.validateClientUser(mail.getDireccionEmail());
        usuario.setDireccionEmail(mail.getDireccionEmail());
        usuario.setValidado(false);
        usuarioRepo.save(usuario);
    }
}
