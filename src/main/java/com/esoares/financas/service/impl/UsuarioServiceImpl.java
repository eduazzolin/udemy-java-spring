package com.esoares.financas.service.impl;

import com.esoares.financas.exception.ErroAutenticacao;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.model.repository.UsuarioRepository;
import com.esoares.financas.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {


   private UsuarioRepository repository;
   private final PasswordEncoder encoder;

   public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
      super();
      this.repository = repository;
      this.encoder = encoder;
   }

   @Override
   public Usuario autenticar(String email, String senha) {
      Optional<Usuario> usuario = repository.findByEmail(email);

      if(usuario.isEmpty()){
         throw new ErroAutenticacao("Usuário não encontrado.");
      }

      boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());


      if(!senhasBatem){
         throw new ErroAutenticacao("Senha inválida.");
      }

      return usuario.get();
   }

   @Override
   @Transactional
   public Usuario salvarUsuario(Usuario usuario) {
      validarEmail(usuario.getEmail());
      criptografarSenha(usuario);
      return repository.save(usuario);
   }

   private void criptografarSenha(Usuario usuario) {
      String senha = usuario.getSenha();
      String senhaCriptografada = encoder.encode(senha);
      usuario.setSenha(senhaCriptografada);
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

   @Override
   public Optional<Usuario> obterPorId(Long id) {
      return repository.findById(id);
   }


}
