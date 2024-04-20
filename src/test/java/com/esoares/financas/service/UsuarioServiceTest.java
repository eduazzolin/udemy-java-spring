package com.esoares.financas.service;

import com.esoares.financas.exception.ErroAutenticacao;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.model.repository.UsuarioRepository;
import com.esoares.financas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {


   @SpyBean
   UsuarioServiceImpl service;
   /*
    * quando o service precisar de um repository ele vai pegar este debaixo,
    * porque "os dois estão no mesmo contexto de injessão de dependência...
    */

   @MockBean
   UsuarioRepository repository;


   @Test()
   public void deveValidarEmail() {
      /*
       * deve rodar sem erros ao validar se um email já existe em um banco vazio.
       */

      // cenário /* QUANDO O MOCK RODAR NO SERVICE ELE VAI RETORNAR FALSE COM QLQ STRING */
      Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

      // verificação / ação
      Assertions.assertThatCode(() -> service.validarEmail("email@email.com")).doesNotThrowAnyException();
   }

   @Test()
   public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
      /*
       * deve lançar exceção ao tentar validar um email que já existe.
       */

      // cenário
      Mockito.when(repository.existsByEmail((Mockito.anyString()))).thenReturn(true);

      // verificação / ação
      Assertions.assertThatThrownBy(() -> service.validarEmail("email@email.com")).isInstanceOf(RegraNegocioException.class);
   }

   @Test
   public void deveAutenticarUmUsuarioComSucesso() {
      String email = "email@email.com";
      String senha = "senha";
      Usuario usuario = Usuario.builder().email(email).senha(senha).build();

      Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
      Usuario result = service.autenticar(email, senha);

      Assertions.assertThat(result).isNotNull();
   }

   @Test
   public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
      Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

      Assertions
              .assertThatThrownBy(() -> service.autenticar("email@email.com", "senha"))
              .isInstanceOf(ErroAutenticacao.class)
              .hasMessage("Usuário não encontrado.");
   }

   @Test
   public void deveLancarErroQuandoSenhaNaoBater() {
      String senha = "senha";
      Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
      Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

      Assertions
              .assertThatThrownBy(() -> service
                      .autenticar("email@email.com", "senha incorreta"))
              .isInstanceOf(ErroAutenticacao.class)
              .hasMessage("Senha inválida.");
   }

   @Test
   public void deveSalvarUsuario() {
      // cenário
      Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
      Usuario usuario = criarUsuarioExemplo();
      usuario.setId(1L);
      Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

      // ação
      Usuario usuarioSalvo = service.salvarUsuario(usuario);

      // verificação
      Assertions.assertThat(usuarioSalvo).isNotNull();
      Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
      Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario");
      Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
      Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
   }

   @Test
   public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
      // cenario
      String mail = "email@email.com";
      Usuario usuario = Usuario.builder().email(mail).build();
      Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(mail);

      // acao verificacao
      Assertions.assertThatThrownBy(() -> service.salvarUsuario(usuario)).isInstanceOf(RegraNegocioException.class);
      Mockito.verify(repository, Mockito.never()).save(usuario);

   }

   private static Usuario criarUsuarioExemplo() {
      return Usuario
              .builder()
              .nome("Usuario")
              .email("usuario@email.com")
              .senha("senha")
              .build();
   }
}
