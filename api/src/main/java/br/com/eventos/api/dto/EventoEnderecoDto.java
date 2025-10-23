// src/main/java/br/com/eventos/api/dto/EventoEnderecoDto.java
package br.com.eventos.api.dto;

import jakarta.validation.constraints.NotBlank;

public record EventoEnderecoDto(
        @NotBlank String cep,
        @NotBlank String logradouro, // do ViaCEP (sem numero)
        @NotBlank String bairro,
        @NotBlank String localidade,
        @NotBlank String uf,
        String numero,
        String complemento
) {}
