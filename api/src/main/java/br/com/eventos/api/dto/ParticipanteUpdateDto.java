package br.com.eventos.api.dto;

public record ParticipanteUpdateDto(
        String nome,
        Integer idTipoParticipante,
        Integer idEndereco,
        String complementoEndereco,
        String nroEndereco
) {}
