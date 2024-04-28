package com.esoares.financas.service;

import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Lancamento;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.model.enums.StatusLancamento;
import com.esoares.financas.model.repository.LancamentoRepository;
import com.esoares.financas.model.repository.LancamentoRepositoryTest;
import com.esoares.financas.service.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

   @SpyBean
   LancamentoServiceImpl service;

   @MockBean
   LancamentoRepository repository;

   @Test
   public void deveSalvarUmLancamento() {
      // cenário
      Lancamento lancamentoASalvar = LancamentoRepositoryTest.gerarLancamentoExemplo();
      Mockito.doNothing().when(service).validar(lancamentoASalvar);

      Lancamento lancamentoSalvo = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamentoSalvo.setId(1L);
      lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
      Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

      //execução
      Lancamento lancamento = service.salvar(lancamentoASalvar);

      // verificação
      assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
      assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
   }

   @Test
   public void deveAtualizarUmLancamento() {
      // cenário
      Lancamento lancamentoSalvo = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamentoSalvo.setId(1L);
      lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

      Mockito.doNothing().when(service).validar(lancamentoSalvo);
      Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

      //execução
      service.atualizar(lancamentoSalvo);

      // verificação
      Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
   }

   @Test
   public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
      // cenário
      Lancamento lancamentoAAtualizar = LancamentoRepositoryTest.gerarLancamentoExemplo();
      Assertions.assertThatThrownBy(() -> service.atualizar((lancamentoAAtualizar))).isInstanceOf(NullPointerException.class);
      Mockito.verify(repository, Mockito.never()).save(lancamentoAAtualizar);
   }

   @Test
   public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
      Lancamento lancamentoASalvar = LancamentoRepositoryTest.gerarLancamentoExemplo();
      Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
      Assertions.assertThatThrownBy(() -> service.salvar(lancamentoASalvar)).isInstanceOf(RegraNegocioException.class);
      Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
   }

   @Test
   public void deveDeletarUmLancamento() {
      // cenário
      Lancamento lancamentoADeletar = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamentoADeletar.setId(1L);

      service.deletar(lancamentoADeletar);

      Mockito.verify(repository).delete(lancamentoADeletar);
   }

   @Test
   public void naoDeveDeletarUmLancamentoQueAindaNaoFoiSalvo() {
      // cenário
      Lancamento lancamentoADeletar = LancamentoRepositoryTest.gerarLancamentoExemplo();

      Assertions.assertThatThrownBy(() -> service.deletar(lancamentoADeletar)).isInstanceOf(NullPointerException.class);

      Mockito.verify(repository, Mockito.never()).delete(lancamentoADeletar);
   }

   @Test
   public void deveFiltrarLancamentos() {
      //cenário
      Lancamento lancamento = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamento.setId(1L);

      List<Lancamento> lista = List.of(lancamento);
      Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

      List<Lancamento> resultado = service.buscar(lancamento);
      Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
   }

   @Test
   public void deveAtualizarOStatusDeUmLancamento() {
      Lancamento lancamento = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamento.setId(1L);
      lancamento.setStatus(StatusLancamento.PENDENTE);

      StatusLancamento status = StatusLancamento.EFETIVADO;
      Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

      service.atualizarStatus(lancamento, status);

      Assertions.assertThat(lancamento.getStatus()).isEqualTo(status);
      Mockito.verify(service).atualizar(lancamento);

   }

   @Test
   public void deveObterUmLancamentoPorId() {
      Long id = 1L;
      Lancamento lancamento = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamento.setId(id);

      Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

      Optional<Lancamento> resultado = service.obterPorId(id);
      Assertions.assertThat(resultado.isPresent()).isTrue();
   }

   @Test
   public void deveRetornarVazioQuandoUmLancamentoNaoExiste() {
      Long id = 1L;
      Lancamento lancamento = LancamentoRepositoryTest.gerarLancamentoExemplo();
      lancamento.setId(id);

      Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

      Optional<Lancamento> resultado = service.obterPorId(id);
      Assertions.assertThat(resultado.isPresent()).isFalse();
   }

   @Test
   public void deveLancarErrosAoValidarUmLancamento() {
      Lancamento lancamento = new Lancamento();

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe uma Descrição válida.");
      lancamento.setDescricao("");

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe uma Descrição válida.");
      lancamento.setDescricao("Descrição");

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Mês válido.");
      lancamento.setMes(-1);
      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Mês válido.");
      lancamento.setMes(13);
      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Mês válido.");
      lancamento.setMes(12);

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Ano válido.");
      lancamento.setAno(0);
      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Ano válido.");
      lancamento.setAno(2000);

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Usuário válido.");
      lancamento.setUsuario(new Usuario());
      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Usuário válido.");
      lancamento.setUsuario(Usuario.builder().id(1L).build());

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Valor válido.");
      lancamento.setValor(BigDecimal.valueOf(-1));
      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Valor válido.");
      lancamento.setValor(BigDecimal.valueOf(20.06));

      assertThatThrownBy(() -> service.validar(lancamento))
              .isInstanceOf(RegraNegocioException.class)
              .hasMessage("Informe um Tipo de lançamento válido.");


   }

}
