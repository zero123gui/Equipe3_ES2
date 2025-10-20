package br.com.eventos.api.dto;

public record EventRegistrationResponseDto(
        Integer id,
        Integer idEvento,
        Integer idTipoInscricao,
        Integer idParticipante
) {}
