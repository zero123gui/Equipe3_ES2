// resposta simples
// src/main/java/br/com/eventos/api/dto/FullRegisterResponse.java
package br.com.eventos.api.dto;

public record FullRegisterResponse(
        Integer participanteId,
        Integer enderecoId,
        String email
) {}
