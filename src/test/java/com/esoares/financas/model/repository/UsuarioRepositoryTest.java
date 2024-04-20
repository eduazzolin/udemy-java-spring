package com.esoares.financas.model.repository;

import com.esoares.financas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;


// @SpringBootTest faz com que a aplicação inteira suba para testar
@DataJpaTest
/* @DataJpaTest
 * cria uma instancia do bd na memoria e remove depois de rodar.
 * é uma transação para cada test e no final é feito um rollback.
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
/* @AutoConfigureTestDatabase
 * É para que o @DataJpaTest use a instancia que está no configTest do bd
 */
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

   @Autowired
   UsuarioRepository repository;

   @Autowired
   TestEntityManager entityManager;


   @Test
   public void deveVerificarAExistenciaDeUmEmail() {
      // cenário
      Usuario usuario = criarUsuarioExemplo();
      entityManager.persist(usuario);

      // ação / execução
      boolean result = repository.existsByEmail("usuario@email.com");

      // verificação
      Assertions.assertThat(result).isTrue();

   }

   @Test
   public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {

      // acao
      boolean result = repository.existsByEmail("usuario@email.com");

      // verificação
      Assertions.assertThat(result).isFalse();
   }

   @Test
   public void devePersistirUmUsuartioNaBaseDeDados() {
      // cenário
      Usuario usuario = criarUsuarioExemplo();

      Usuario usuarioSalvo = repository.save(usuario);

      Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
   }

   private static Usuario criarUsuarioExemplo() {
      return Usuario
              .builder()
              .nome("Usuario")
              .email("usuario@email.com")
              .senha("senha")
              .build();
   }

   @Test
   public void deveBuscarUmUsuarioPorEmail() {
      Usuario usuario = criarUsuarioExemplo();
      entityManager.persist(usuario);

      // verificação
      Optional<Usuario> result = repository.findByEmail("usuario@email.com");
      Assertions.assertThat(result.isPresent()).isTrue();

   }

   @Test
   public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
      // verificação
      Optional<Usuario> result = repository.findByEmail("usuario@email.com");
      Assertions.assertThat(result.isPresent()).isFalse();

   }

}
