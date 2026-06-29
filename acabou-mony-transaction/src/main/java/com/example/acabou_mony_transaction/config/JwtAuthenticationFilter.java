package com.example.acabou_mony_transaction.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. TODO: Validar o seu token aqui usando sua classe de Token/JWT
            // Exemplo fictício: boolean isValid = tokenService.validateToken(token);
            boolean isTokenValido = true; // Altere para a sua lógica real de validação

            if (isTokenValido) {
                // Extrai o usuário do token (ex: "felipe.souza" ou o id)
                String username = "felipe.souza";

                // 3. Informa o Spring Security que o usuário está autenticado
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.emptyList() // Lista de permissões/roles se houver
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Coloca o usuário no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continua o fluxo normal da requisição
        filterChain.doFilter(request, response);
    }
}