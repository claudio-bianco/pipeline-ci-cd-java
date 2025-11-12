package com.claudiobianco.java.renderapi.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do ItemController
 * - Sobe o contexto do Spring Boot
 * - Usa MockMvc para chamar os endpoints reais
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarItemERecuperarPorId() throws Exception {
        String body = """
                {
                  "nome": "Notebook Dell",
                  "descricao": "Laptop com 16GB RAM",
                  "status": "ATIVO",
                  "preco": 4500.0,
                  "categoria": "ELETRONICOS"
                }
                """;

        // cria
        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook Dell"));

        // busca por id
        mockMvc.perform(get("/api/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook Dell"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveListarItensPaginados() throws Exception {
        String body1 = """
                {
                  "nome": "Item 1",
                  "descricao": "Primeiro",
                  "status": "ATIVO",
                  "preco": 10.0,
                  "categoria": "GERAL"
                }
                """;

        String body2 = """
                {
                  "nome": "Item 2",
                  "descricao": "Segundo",
                  "status": "INATIVO",
                  "preco": 20.0,
                  "categoria": "GERAL"
                }
                """;

        // cria 2 itens
        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body1));

        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body2));

        // lista
        mockMvc.perform(get("/api/v1/items?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[1].id").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void deveRetornar404AoBuscarItemInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    void deveAtualizarItemComSucesso() throws Exception {
        String bodyCriar = """
                {
                  "nome": "Teclado",
                  "descricao": "Teclado mecânico",
                  "status": "ATIVO",
                  "preco": 300.0,
                  "categoria": "ACESSORIOS"
                }
                """;

        String bodyAtualizar = """
                {
                  "nome": "Teclado Mecânico RGB",
                  "descricao": "Atualizado com iluminação RGB",
                  "status": "ATIVO",
                  "preco": 350.0,
                  "categoria": "ACESSORIOS"
                }
                """;

        // cria
        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyCriar))
                .andExpect(status().isCreated());

        // atualiza
        mockMvc.perform(put("/api/v1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyAtualizar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Teclado Mecânico RGB"))
                .andExpect(jsonPath("$.preco").value(350.0));
    }

    @Test
    void deveDeletarItemEConfirmarQueNaoExisteMais() throws Exception {
        String body = """
                {
                  "nome": "Headset",
                  "descricao": "Headset com microfone",
                  "status": "ATIVO",
                  "preco": 250.0,
                  "categoria": "ACESSORIOS"
                }
                """;

        // cria
        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        // deleta
        mockMvc.perform(delete("/api/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deleted").value(true));

        // tenta buscar de novo
        mockMvc.perform(get("/api/v1/items/1"))
                .andExpect(status().isNotFound());
    }
}
