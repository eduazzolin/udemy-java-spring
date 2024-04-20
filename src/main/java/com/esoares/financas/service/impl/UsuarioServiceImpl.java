package com.esoares.financas.service.impl;

import com.esoares.financas.exception.ErroAutenticacao;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.model.repository.UsuarioRepository;
import com.esoares.financas.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {


   private UsuarioRepository repository;

   public UsuarioServiceImpl(UsuarioRepository repository) {
      super();
      this.repository = repository;
   }

   @Override
   public Usuario autenticar(String email, String senha) {
      Optional<Usuario> usuario = repository.findByEmail(email);

      if(usuario.isEmpty()){
         throw new ErroAutenticacao("Usuário não encontrado.");
      }

      if(!usuario.get().getSenha().equals(senha)){
         throw new ErroAutenticacao("Senha inválida.");
      }

      return usuario.get();
   }

   @Override
   @Transactional
   public Usuario salvarUsuario(Usuario usuario) {
      validarEmail(usuario.getEmail());
      return repository.save(usuario);
   }

   @Override
   /**
    * lança exceção se o email recebido por parâmetro já existir
    */
   public void validarEmail(String email) {
      boolean existe = repository.existsByEmail(email);
      if(existe){
         throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
      }
   }


}
