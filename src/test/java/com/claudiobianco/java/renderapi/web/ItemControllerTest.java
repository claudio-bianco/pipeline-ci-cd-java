package com.claudiobianco.java.renderapi.web;

import com.claudiobianco.java.renderapi.domain.ItemNotFoundException;
import com.claudiobianco.java.renderapi.domain.ItemService;
import com.claudiobianco.java.renderapi.web.dto.ItemRequest;
import com.claudiobianco.java.renderapi.web.dto.ItemResponse;
import com.claudiobianco.java.renderapi.web.dto.PagedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@Import(ApiExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService service;

    private ItemResponse resp(Long id, String nome) {
        return new ItemResponse(
                id, nome, "Descricao", "ATIVO", 123.45, "GERAL",
                "2025-01-01T00:00:00Z", "2025-01-01T00:00:00Z"
        );
    }

    private ItemRequest req(String nome, double preco) {
        return new ItemRequest(nome, "Descricao", "ATIVO", preco, "GERAL");
    }

    @Test
    @DisplayName("GET /api/v1/items - deve listar itens paginados (vazio)")
    void deveListarVazio() throws Exception {
        PagedResponse<ItemResponse> page = new PagedResponse<>(List.of(), 0, 10, 0, 0);
        when(service.list(0, 10)).thenReturn(page);

        mvc.perform(get("/api/v1/items?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.totalItems", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    @DisplayName("GET /api/v1/items - deve listar itens paginados (com 2 itens)")
    void deveListarComItens() throws Exception {
        PagedResponse<ItemResponse> page = new PagedResponse<>(
                List.of(resp(1L, "A"), resp(2L, "B")), 0, 10, 2, 1
        );
        when(service.list(0, 10)).thenReturn(page);

        mvc.perform(get("/api/v1/items?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[1].id", is(2)))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/items/{id} - deve retornar item por id")
    void deveBuscarPorId() throws Exception {
        when(service.get(1L)).thenReturn(resp(1L, "Teclado"));

        mvc.perform(get("/api/v1/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Teclado")));
    }

    @Test
    @DisplayName("POST /api/v1/items - deve criar item com sucesso (201)")
    void deveCriarItemComSucesso() throws Exception {
        ItemRequest body = req("Mouse", 199.9);
        ItemResponse created = resp(10L, "Mouse");
        when(service.create(any(ItemRequest.class))).thenReturn(created);

        mvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nome", is("Mouse")));
    }

    @Test
    @DisplayName("PUT /api/v1/items/{id} - deve atualizar item com sucesso (200)")
    void deveAtualizarItemComSucesso() throws Exception {
        ItemRequest body = req("Mouse Pro", 249.9);
        ItemResponse updated = resp(10L, "Mouse Pro");
        when(service.update(eq(10L), any(ItemRequest.class))).thenReturn(updated);

        mvc.perform(put("/api/v1/items/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nome", is("Mouse Pro")));
    }

    @Test
    @DisplayName("DELETE /api/v1/items/{id} - deve deletar item com sucesso (200)")
    void deveDeletarItemComSucesso() throws Exception {
        doNothing().when(service).delete(1L);

        mvc.perform(delete("/api/v1/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(true)))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/items/{id} - deve retornar 404 quando não encontrar")
    void deveRetornar404QuandoNaoEncontrarItem() throws Exception {
        when(service.get(999L)).thenThrow(new ItemNotFoundException(999L));

        mvc.perform(get("/api/v1/items/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("ITEM_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("PUT /api/v1/items/{id} - deve retornar 404 ao atualizar inexistente")
    void deveRetornar404AoAtualizarItemInexistente() throws Exception {
        ItemRequest body = req("X", 10.0);
        when(service.update(eq(999L), any(ItemRequest.class)))
                .thenThrow(new ItemNotFoundException(999L));

        mvc.perform(put("/api/v1/items/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("ITEM_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("DELETE /api/v1/items/{id} - deve retornar 404 ao deletar inexistente")
    void deveRetornar404AoDeletarItemInexistente() throws Exception {
        doThrow(new ItemNotFoundException(999L)).when(service).delete(999L);

        mvc.perform(delete("/api/v1/items/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("ITEM_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    @DisplayName("GET /api/v1/items?page=2&size=1 - deve respeitar paginação")
    void deveRespeitarPaginacao() throws Exception {
        PagedResponse<ItemResponse> page = new PagedResponse<>(
                List.of(resp(3L, "C")), 2, 1, 3, 3
        );
        when(service.list(2, 1)).thenReturn(page);

        mvc.perform(get("/api/v1/items?page=2&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.totalItems", is(3)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.items[0].id", is(3)));
    }

    @Test
    @DisplayName("DELETE /api/v1/items/{id} - contrato de resposta (chaves básicas)")
    void contratoDelete() throws Exception {
        doNothing().when(service).delete(5L);

        mvc.perform(delete("/api/v1/items/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$", allOf(
                        hasKey("id"),
                        hasKey("deleted"),
                        hasKey("message")
                )));
    }
}

