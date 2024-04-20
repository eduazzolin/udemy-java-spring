package com.esoares.financas.api.resource;

import com.esoares.financas.api.dto.UsuarioDTO;
import com.esoares.financas.exception.ErroAutenticacao;
import com.esoares.financas.exception.RegraNegocioException;
import com.esoares.financas.model.entity.Usuario;
import com.esoares.financas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

   private UsuarioService service;

   public UsuarioResource(UsuarioService service) {
      this.service = service;
   }

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
   public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
      try {
         Usuario usurioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
         return ResponseEntity.ok(usurioAutenticado);
      } catch (ErroAutenticacao e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      }
   }

}
