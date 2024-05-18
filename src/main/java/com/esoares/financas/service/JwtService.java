package com.esoares.financas.service;

import com.esoares.financas.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;


public interface JwtService {

   String generateToken(Usuario usuario);

   Claims obterClaims(String token) throws ExpiredJwtException;

   boolean isTokenValido(String token);

   String obterLoginUsuario(String token);

}