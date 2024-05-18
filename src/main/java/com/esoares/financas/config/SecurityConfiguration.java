package com.esoares.financas.config;

import com.esoares.financas.api.JwtTokenFilter;
import com.esoares.financas.service.JwtService;
import com.esoares.financas.service.impl.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

   private final SecurityUserDetailsService userDetailsService;
   private final JwtService jwtService;

   @Autowired
   public SecurityConfiguration(SecurityUserDetailsService userDetailsService, JwtService jwtService) {
      this.userDetailsService = userDetailsService;
      this.jwtService = jwtService;
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

      auth
              .userDetailsService(userDetailsService)
              .passwordEncoder(passwordEncoder());
   }

   @Bean
   public JwtTokenFilter jwtTokenFilter() {
      return new JwtTokenFilter(jwtService, userDetailsService);
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(authz -> authz
                      .requestMatchers(
                              new AntPathRequestMatcher("/api/usuarios/autenticar", HttpMethod.POST.name()),
                              new AntPathRequestMatcher("/api/usuarios", HttpMethod.POST.name())
                      ).permitAll()
                      .anyRequest().authenticated()
              )
              .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

      return http.build();
   }
}
