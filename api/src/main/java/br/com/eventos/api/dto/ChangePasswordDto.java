package br.com.eventos.api.dto;

public record ChangePasswordDto(
        String senhaAtual,
        String novaSenha
) {}
