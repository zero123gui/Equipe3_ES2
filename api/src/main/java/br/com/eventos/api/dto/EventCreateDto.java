// src/main/java/br/com/eventos/api/dto/EventCreateDto.java
package br.com.eventos.api.dto;

import java.time.LocalDate;

public record EventCreateDto(
        String nomeEvento,
        LocalDate dtInicio,
        LocalDate dtTermino,
        String local,
        String descricao,
        String urlSite
) {}
