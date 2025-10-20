// src/main/java/br/com/eventos/api/dto/AddressInDto.java
package br.com.eventos.api.dto;

public record AddressInDto(
        String cep,
        String logradouro,   // ex.: "Avenida Brasil" ou "Brasil" (sem tipo)
        String bairro,
        String localidade,   // cidade
        String uf,           // "PR", "SP", ...
        String numero,
        String complemento
) {}
