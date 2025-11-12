package com.claudiobianco.java.renderapi.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Cada teste recebe um ItemController NOVO (store em memória zerado)
        ItemController controller = new ItemController();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void deveCriarItemComSucesso() throws Exception {
        String body = """
                {
                  "nome": "Notebook Dell",
                  "descricao": "Laptop com 16GB RAM",
                  "status": "ATIVO",
                  "preco": 4500.0,
                  "categoria": "ELETRONICOS"
                }
                """;

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Notebook Dell"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.preco").value(4500.0))
                .andExpect(jsonPath("$.categoria").value("ELETRONICOS"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
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
    void deveBuscarItemPorId() throws Exception {
        String body = """
                {
                  "nome": "Mouse Gamer",
                  "descricao": "RGB 16000 DPI",
                  "status": "ATIVO",
                  "preco": 199.9,
                  "categoria": "ACESSORIOS"
                }
                """;

        // cria
        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // busca por id
        mockMvc.perform(get("/api/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mouse Gamer"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar404QuandoNaoEncontrarItem() throws Exception {
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
                .content(bodyCriar));

        // atualiza
        mockMvc.perform(put("/api/v1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyAtualizar))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Teclado Mecânico RGB"))
                .andExpect(jsonPath("$.preco").value(350.0))
                .andExpect(jsonPath("$.descricao").value("Atualizado com iluminação RGB"));
    }

    @Test
    void deveDeletarItemComSucesso() throws Exception {
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
                .content(body));

        // deleta
        mockMvc.perform(delete("/api/v1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.message").value("Item removido com sucesso"));

        // garante que não existe mais
        mockMvc.perform(get("/api/v1/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoAtualizarItemInexistente() throws Exception {
        String bodyAtualizar = """
                {
                "nome": "Qualquer",
                "descricao": "Não importa",
                "status": "ATIVO",
                "preco": 100.0,
                "categoria": "GERAL"
                }
                """;

        mockMvc.perform(put("/api/v1/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyAtualizar))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    void deveRetornar404AoDeletarItemInexistente() throws Exception {
        mockMvc.perform(delete("/api/v1/items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    void deveListarItensQuandoNaoHaNenhum() throws Exception {
        // Nenhum POST antes, store vazio
        mockMvc.perform(get("/api/v1/items?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void deveRetornarPaginaVaziaQuandoPaginaMaiorQueTotal() throws Exception {
        String body = """
                {
                "nome": "Item único",
                "descricao": "Teste paginação",
                "status": "ATIVO",
                "preco": 10.0,
                "categoria": "GERAL"
                }
                """;

        // cria 1 item
        mockMvc.perform(post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        // pede page=1 com size=10 (desde que só haja 1 item, page=0 contém o item, page=1 deve vir vazia)
        mockMvc.perform(get("/api/v1/items?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

}