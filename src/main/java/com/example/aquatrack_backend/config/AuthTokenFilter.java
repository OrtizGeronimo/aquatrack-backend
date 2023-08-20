package com.example.aquatrack_backend.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.aquatrack_backend.dto.ErrorResponseDTO;
import com.example.aquatrack_backend.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String jwt = parseJwt(request.getHeader("Authorization"));
        final String direccionEmail = jwtUtils.getUserNameFromJwtToken(jwt);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(direccionEmail);
        
        if (jwt == null || !jwtUtils.isTokenValid(jwt, userDetails)){
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter()
                  .write(new ObjectMapper().writeValueAsString(ErrorResponseDTO.builder()
                                                                               .message("Debe estar logueado para realizar esta acción.")
                                                                               .build()));
          return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                         null,
                                                                                                     userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private String parseJwt(String headerAuth) {

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.split(" ")[1];
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
      String path = request.getRequestURI();
      return Arrays.asList("/users/login", "/users/register")
                   .stream().anyMatch(p -> p.equals(path));
    }
}
