package br.com.eventos.api.dto;

public record EventRegistrationCreateDto(
        Integer idEvento,
        Integer idTipoInscricao
) {}
