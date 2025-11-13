package com.claudiobianco.java.renderapi.domain;

import com.claudiobianco.java.renderapi.web.dto.ItemRequest;
import com.claudiobianco.java.renderapi.web.dto.ItemResponse;
import com.claudiobianco.java.renderapi.web.dto.PagedResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    private final ItemService service = new ItemService();

    private ItemRequest req(String nome, double preco) {
        return new ItemRequest(nome, "Desc", "ATIVO", preco, "GERAL");
    }

    @Test
    void criaEBuscaItem() {
        ItemResponse created = service.create(req("Mouse", 100.0));
        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("Mouse", created.nome());
        assertNotNull(created.createdAt());
        assertNotNull(created.updatedAt());

        ItemResponse got = service.get(created.id());
        assertEquals(created.id(), got.id());
        assertEquals("Mouse", got.nome());
    }

    @Test
    void atualizaItemMantendoCreatedAt() {
        ItemResponse created = service.create(req("Teclado", 200.0));
        Long id = created.id();

        ItemResponse updated = service.update(id, req("Teclado Pro", 250.0));
        assertEquals(id, updated.id());
        assertEquals("Teclado Pro", updated.nome());
        assertEquals(created.createdAt(), updated.createdAt());
        assertNotEquals(created.updatedAt(), updated.updatedAt());
    }

    @Test
    void deletaItemComSucesso() {
        ItemResponse created = service.create(req("HD", 300.0));
        Long id = created.id();
        assertDoesNotThrow(() -> service.delete(id));
        assertThrows(ItemNotFoundException.class, () -> service.get(id));
    }

    @Test
    void getUpdateDeleteLancaNotFound() {
        assertThrows(ItemNotFoundException.class, () -> service.get(999L));
        assertThrows(ItemNotFoundException.class, () -> service.update(999L, req("X", 1.0)));
        assertThrows(ItemNotFoundException.class, () -> service.delete(999L));
    }

    @Test
    void listaPaginadaVazia() {
        PagedResponse<ItemResponse> page = service.list(0, 10);
        assertNotNull(page);
        assertEquals(0, page.totalItems());
        assertEquals(0, page.totalPages());
        assertTrue(page.items().isEmpty());
    }

    @Test
    void listaPaginadaComElementosEPageForaDoLimite() {
        // cria 3 itens
        service.create(req("A", 1.0));
        service.create(req("B", 2.0));
        service.create(req("C", 3.0));

        // size 1 -> 3 páginas
        PagedResponse<ItemResponse> p0 = service.list(0, 1);
        assertEquals(1, p0.items().size());
        assertEquals(3, p0.totalItems());
        assertEquals(3, p0.totalPages());

        // página além do limite -> sublista vazia, mas metadados coerentes
        PagedResponse<ItemResponse> p99 = service.list(99, 1);
        assertEquals(0, p99.items().size());
        assertEquals(3, p99.totalItems());
        assertEquals(3, p99.totalPages());

        // size=0 (evita divisão por zero no service, totalPages=0)
        PagedResponse<ItemResponse> pSizeZero = service.list(0, 0);
        assertEquals(0, pSizeZero.totalPages());
        // itens não devem explodir
        assertNotNull(pSizeZero.items());
    }
}
