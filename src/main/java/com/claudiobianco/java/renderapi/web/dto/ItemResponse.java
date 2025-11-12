package com.claudiobianco.java.renderapi.web.dto;

public record ItemResponse(
        Long id,
        String nome,
        String descricao,
        String status,
        Double preco,
        String categoria,
        String createdAt,
        String updatedAt
) {}

