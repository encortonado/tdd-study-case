
package com.bortolo.softwarequalityclass.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class MensagemRequest {
  private String usuario;
  private String conteudo;
}
