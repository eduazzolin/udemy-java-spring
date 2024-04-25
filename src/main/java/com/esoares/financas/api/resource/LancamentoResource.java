package com.esoares.financas.api.resource;

import com.esoares.financas.api.dto.AtualizaStatusDTO;
import com.esoares.financas.api.dto.LancamentoDTO;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Lancamento;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.model.enums.StatusLancamento;
import com.esoares.financas.model.enums.TipoLancamento;
import com.esoares.financas.service.LancamentoService;
import com.esoares.financas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

   private final LancamentoService service;
   private final UsuarioService usuarioService;

   @GetMapping
   public ResponseEntity buscar(
           @RequestParam(value = "descricao", required = false) String descricao,
           @RequestParam(value = "mes", required = false) Integer mes,
           @RequestParam(value = "ano", required = false) Integer ano,
           @RequestParam(value = "usuario") Long idUsuario) {

      Lancamento lancamentoFiltro = new Lancamento();
      lancamentoFiltro.setDescricao(descricao);
      lancamentoFiltro.setMes(mes);
      lancamentoFiltro.setAno(ano);

      Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
      if (usuario.isEmpty()) {
         return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. " +
                 "Usuário não encontrado para o ID informado..");
      } else {
         lancamentoFiltro.setUsuario(usuario.get());
      }

      List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
      return ResponseEntity.ok(lancamentos);
   }

   @PutMapping("{id}/atualiza-status")
   public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
      return service.obterPorId(id).map( entity ->{
         StatusLancamento statusSelecionado = null;
         try {
            statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
         } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
         }
         try {
            entity.setStatus(statusSelecionado);
            service.atualizar(entity);
            return ResponseEntity.ok(entity);
         } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
         }
      }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
   }

   @PostMapping
   public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
      try {
         Lancamento entidade = converter(dto);
         entidade = service.salvar(entidade);
         return new ResponseEntity(entidade, HttpStatus.CREATED);
      } catch (RegraNegocioException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      }

   }

   @PutMapping("{id}")
   public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto) {
      return service.obterPorId(id).map(entity -> {
         try {
            Lancamento lancamento = converter(dto);
            lancamento.setId(entity.getId());
            service.atualizar(lancamento);
            return ResponseEntity.ok(lancamento);
         } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
         }
      }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
   }

   @DeleteMapping("{id}")
   public ResponseEntity deletar(@PathVariable("id") Long id) {
      return service.obterPorId(id).map(entidade -> {
         service.deletar(entidade);
         return new ResponseEntity(HttpStatus.NO_CONTENT);
      }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
   }


   private Lancamento converter(LancamentoDTO dto) {
      Lancamento lancamento = new Lancamento();

      lancamento.setId(dto.getId());
      lancamento.setDescricao(dto.getDescricao());
      lancamento.setAno(dto.getAno());
      lancamento.setMes(dto.getMes());
      lancamento.setValor(dto.getValor());

      Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
              .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado."));

      lancamento.setUsuario(usuario);
      if (dto.getTipo() != null) {
         lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
      }

      if (dto.getStatus() != null) {
         lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
      }


      return lancamento;
   }

}
