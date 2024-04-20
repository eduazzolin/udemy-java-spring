package com.esoares.financas.service;

import com.esoares.financas.model.entity.Usuario;

public interface UsuarioService {

   Usuario autenticar(String email, String senha);

   Usuario salvarUsuario(Usuario usuario);

   void validarEmail(String email);


}
