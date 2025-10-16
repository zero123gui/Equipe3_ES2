package br.com.eventos.api.dto;

public record RegisterDto(
        Integer participanteId,
        String email,
        String senha
) {}
