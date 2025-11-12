package com.claudiobianco.java.renderapi.web.dto;

public record ItemRequest(
        String nome,
        String descricao,
        String status,
        Double preco,
        String categoria
) {}
