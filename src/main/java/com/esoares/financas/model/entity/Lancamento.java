package com.esoares.financas.model.entity;

import com.esoares.financas.model.enums.StatusLancamento;
import com.esoares.financas.model.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "lancamento")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lancamento {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
   private Long id;

   @Column(name = "descricao")
   private String descricao;

   @Column(name = "mes")
   private Integer mes;

   @Column(name = "ano")
   private Integer ano;

   @ManyToOne
   @JoinColumn(name = "id_usuario")
   private Usuario usuario;

   @Column(name = "valor")
   private BigDecimal valor;

   @Column(name = "data_cadastro")
   private LocalDate dataCadastro;

   @Column(name = "tipo")
   @Enumerated(value = EnumType.STRING)
   private TipoLancamento tipo;

   @Column(name = "status")
   @Enumerated(value = EnumType.STRING)
   private StatusLancamento status;

}
