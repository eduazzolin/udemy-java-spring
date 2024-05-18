package com.esoares.financas.api;

import com.esoares.financas.service.JwtService;
import com.esoares.financas.service.impl.SecurityUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

   private final JwtService jwtService;
   private final SecurityUserDetailsService userDetailsService;

   public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailsService) {
      this.jwtService = jwtService;
      this.userDetailsService = userDetailsService;
   }

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      String authorization = request.getHeader("Authorization");

      if (authorization != null && authorization.startsWith("Bearer")) {
         String token = authorization.substring(7);
         boolean isValid = jwtService.isTokenValido(token);

         if (isValid) {
            String username = jwtService.obterLoginUsuario(token);
            UserDetails usuarioAutenticado = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                    usuarioAutenticado,
                    null,
                    usuarioAutenticado.getAuthorities());
            user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(user);
         }
      }
      filterChain.doFilter(request, response);


   }
}
