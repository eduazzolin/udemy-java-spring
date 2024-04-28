package com.esoares.financas.model.repository;

import com.esoares.financas.model.entity.Lancamento;
import com.esoares.financas.model.enums.StatusLancamento;
import com.esoares.financas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

   @Autowired
   LancamentoRepository repository;

   @Autowired
   TestEntityManager entityManager;

   public static Lancamento gerarLancamentoExemplo() {
      return Lancamento.builder()
              .ano(2019)
              .mes(1)
              .descricao("Lancamento ex")
              .valor(BigDecimal.valueOf(10))
              .tipo(TipoLancamento.RECEITA)
              .status(StatusLancamento.PENDENTE)
              .dataCadastro(LocalDate.now())
              .build();
   }

   @Test
   public void deveSalvarUmLancamento() {
      Lancamento lancamento = gerarLancamentoExemplo();
      lancamento = repository.save(lancamento);

      assertThat(lancamento.getId()).isNotNull();

   }

   @Test
   public void deveDeletarUmLancamento() {
      Lancamento lancamento = gerarLancamentoEPersistirLancamento();

      repository.delete(lancamento);

      Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
      assertThat(lancamentoInexistente).isNull();
   }

   private Lancamento gerarLancamentoEPersistirLancamento() {
      Lancamento lancamento = gerarLancamentoExemplo();
      entityManager.persist(lancamento);
      lancamento = entityManager.find(Lancamento.class, lancamento.getId());
      return lancamento;
   }

   @Test
   public void deveAtualizarUmLancamento() {
      Lancamento lancamento = gerarLancamentoEPersistirLancamento();

      lancamento.setAno(2000);
      lancamento.setStatus(StatusLancamento.CANCELADO);
      lancamento.setDescricao("MODIFICADO");

      repository.save(lancamento);

      Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

      assertThat(lancamentoAtualizado.getAno()).isEqualTo(2000);
      assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
      assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("MODIFICADO");

   }

   @Test
   public void deveBuscarUmLancamentoPorId() {
      Lancamento lancamento = gerarLancamentoEPersistirLancamento();
      Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

      assertThat(lancamentoEncontrado.isPresent()).isTrue();

   }


}
