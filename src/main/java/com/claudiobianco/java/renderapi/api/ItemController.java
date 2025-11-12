package com.claudiobianco.java.renderapi.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    // "Banco de dados" em memória só para exemplo
    private final Map<Long, ItemResponse> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    // ========= LISTAR (READ - GET /items) =========
    @GetMapping
    public PagedResponse<ItemResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Listando itens - page={}, size={}", page, size);

        List<ItemResponse> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparingLong(ItemResponse::getId)); // opcional: ordena por id

        int from = page * size;
        int to = Math.min(from + size, all.size());
        if (from > all.size()) {
            from = all.size();
            to = all.size();
        }

        List<ItemResponse> pageItems = all.subList(from, to);
        int totalPages = (int) Math.ceil((double) all.size() / size);

        return new PagedResponse<>(
                pageItems,
                page,
                size,
                all.size(),
                totalPages
        );
    }

    // ========= BUSCAR POR ID (READ - GET /items/{id}) =========
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("Buscando item id={}", id);
        ItemResponse item = store.get(id);
        if (item == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "ITEM_NOT_FOUND",
                            "message", "Item com id=" + id + " não encontrado"
                    )
            );
        }
        return ResponseEntity.ok(item);
    }

    // ========= CRIAR (CREATE - POST /items) =========
    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody ItemRequest request) {
        long id = idGenerator.incrementAndGet();
        Instant now = Instant.now();

        ItemResponse item = new ItemResponse(
                id,
                request.getNome(),
                request.getDescricao(),
                request.getStatus(),
                request.getPreco(),
                request.getCategoria(),
                now.toString(),
                now.toString()
        );

        store.put(id, item);
        log.info("Item criado id={}", id);

        return ResponseEntity.status(201).body(item);
    }

    // ========= ATUALIZAR (UPDATE - PUT /items/{id}) =========
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ItemRequest request
    ) {
        ItemResponse existing = store.get(id);
        if (existing == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "ITEM_NOT_FOUND",
                            "message", "Item com id=" + id + " não encontrado"
                    )
            );
        }

        Instant now = Instant.now();

        ItemResponse updated = new ItemResponse(
                id,
                request.getNome(),
                request.getDescricao(),
                request.getStatus(),
                request.getPreco(),
                request.getCategoria(),
                existing.getCreatedAt(), // mantém data de criação
                now.toString()           // atualiza updatedAt
        );

        store.put(id, updated);
        log.info("Item atualizado id={}", id);

        return ResponseEntity.ok(updated);
    }

    // ========= DELETAR (DELETE - DELETE /items/{id}) =========
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ItemResponse removed = store.remove(id);
        if (removed == null) {
            return ResponseEntity.status(404).body(
                    Map.of(
                            "error", "ITEM_NOT_FOUND",
                            "message", "Item com id=" + id + " não encontrado"
                    )
            );
        }

        log.info("Item removido id={}", id);

        return ResponseEntity.ok(
                Map.of(
                        "id", id,
                        "deleted", true,
                        "message", "Item removido com sucesso"
                )
        );
    }

    // ===================== DTOs =====================

    // Request para CREATE/UPDATE
    public static class ItemRequest {
        private String nome;
        private String descricao;
        private String status;
        private Double preco;
        private String categoria;

        // Getters e setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getPreco() { return preco; }
        public void setPreco(Double preco) { this.preco = preco; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }
    }

    // Response padrão do Item
    public static class ItemResponse {
        private Long id;
        private String nome;
        private String descricao;
        private String status;
        private Double preco;
        private String categoria;
        private String createdAt;
        private String updatedAt;

        public ItemResponse(
                Long id,
                String nome,
                String descricao,
                String status,
                Double preco,
                String categoria,
                String createdAt,
                String updatedAt
        ) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.status = status;
            this.preco = preco;
            this.categoria = categoria;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public ItemResponse() {}

        // Getters e setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getPreco() { return preco; }
        public void setPreco(Double preco) { this.preco = preco; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    // Wrapper de paginação
    public static class PagedResponse<T> {
        private List<T> items;
        private int page;
        private int size;
        private long totalItems;
        private int totalPages;

        public PagedResponse(List<T> items, int page, int size, long totalItems, int totalPages) {
            this.items = items;
            this.page = page;
            this.size = size;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }

        public PagedResponse() {}

        public List<T> getItems() { return items; }
        public void setItems(List<T> items) { this.items = items; }

        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
