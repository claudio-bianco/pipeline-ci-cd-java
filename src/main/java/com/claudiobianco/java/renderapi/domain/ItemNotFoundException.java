package com.claudiobianco.java.renderapi.domain;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("Item com id=" + id + " não encontrado");
    }
}
