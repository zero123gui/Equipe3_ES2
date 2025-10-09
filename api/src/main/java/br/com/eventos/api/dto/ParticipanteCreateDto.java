package br.com.eventos.api.dto;

public record ParticipanteCreateDto(
        String nome,
        Integer idTipoParticipante,
        Integer idEndereco,
        String complementoEndereco,
        String nroEndereco
) {}
