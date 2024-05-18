package com.esoares.financas.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUserDetailsService implements UserDetailsService {

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return null;
   }
}
