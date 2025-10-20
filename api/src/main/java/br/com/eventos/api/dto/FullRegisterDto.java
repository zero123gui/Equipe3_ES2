// src/main/java/br/com/eventos/api/dto/FullRegisterDto.java
package br.com.eventos.api.dto;

public record FullRegisterDto(
        String nome,
        String email,
        String telefone,          // pode vir "(45) 99999-0000" ou "45999990000"
        String senha,             // texto puro; vamos hashear
        Integer idTipoParticipante,
        AddressInDto endereco
) {}
