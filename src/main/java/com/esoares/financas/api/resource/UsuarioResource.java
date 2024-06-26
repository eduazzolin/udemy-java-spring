package com.esoares.financas.api.resource;

import com.esoares.financas.api.dto.TokenDTO;
import com.esoares.financas.api.dto.UsuarioDTO;
import com.esoares.financas.exception.ErroAutenticacao;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.service.JwtService;
import com.esoares.financas.service.LancamentoService;
import com.esoares.financas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

   private final UsuarioService service;
   private final LancamentoService lancamentoService;
   private final JwtService jwtService;

   @PostMapping
   public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {

      Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();

      try {
         Usuario usuarioSalvo = service.salvarUsuario(usuario);
         return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
      } catch (RegraNegocioException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      }

   }

   @PostMapping("/autenticar")
   public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
      try {
         Usuario usurioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
         TokenDTO tokenDTO = new TokenDTO(usurioAutenticado.getNome(), jwtService.generateToken(usurioAutenticado));


         return ResponseEntity.ok(tokenDTO);
      } catch (ErroAutenticacao e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      }
   }

   @GetMapping("{id}/saldo")
   public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
      Optional<Usuario> usuario = service.obterPorId(id);
      if (usuario.isEmpty()) {
         return new ResponseEntity(HttpStatus.NOT_FOUND);
      }
      BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
      return ResponseEntity.ok(saldo);
   }

}
