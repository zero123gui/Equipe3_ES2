package br.com.eventos.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TalkUpdateDto(
        String nomePalestra,
        String local,
        String descricao,
        Integer qtdVagas,
        LocalDate dtInicio,
        LocalDate dtTermino,
        LocalTime horaInicio,
        LocalTime horaTermino,
        Integer idEvento
) {}
