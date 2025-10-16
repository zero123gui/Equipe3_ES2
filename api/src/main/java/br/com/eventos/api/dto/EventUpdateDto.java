// src/main/java/br/com/eventos/api/dto/EventUpdateDto.java
package br.com.eventos.api.dto;

import java.time.LocalDate;

public record EventUpdateDto(
        String nomeEvento,
        LocalDate dtInicio,
        LocalDate dtTermino,
        String local,
        String descricao,
        String urlSite
) {}
