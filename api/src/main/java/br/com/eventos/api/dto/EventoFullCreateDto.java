// src/main/java/br/com/eventos/api/dto/EventoFullCreateDto.java
package br.com.eventos.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EventoFullCreateDto(
        @NotBlank @Size(max = 40) String nomeEvento,
        @NotNull LocalDate dtInicio,
        @NotNull LocalDate dtTermino,
        @Size(max = 100) String descricao,
        @Size(max = 50)  String urlSite,
        @NotNull @Valid  EventoEnderecoDto endereco
) {}
