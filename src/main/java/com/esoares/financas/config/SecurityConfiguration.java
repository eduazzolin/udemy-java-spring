package com.esoares.financas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
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
              .httpBasic(withDefaults())
              .formLogin(AbstractHttpConfigurer::disable); // Disables form login configuration

      return http.build();
   }

   @Bean
   public InMemoryUserDetailsManager userDetailsService() {
      UserDetails user = User.withUsername("financas")
              .password(passwordEncoder().encode("qwe123"))
              .roles("USER")
              .build();
      return new InMemoryUserDetailsManager(user);
   }
}