package com.claudiobianco.java.renderapi.web;

import com.claudiobianco.java.renderapi.domain.ItemService;
import com.claudiobianco.java.renderapi.web.dto.ItemRequest;
import com.claudiobianco.java.renderapi.web.dto.ItemResponse;
import com.claudiobianco.java.renderapi.web.dto.PagedResponse;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private final ItemService service = new ItemService(); // simples para demo; em produção, @Service + @Autowired

    @GetMapping
    public PagedResponse<ItemResponse> list(@RequestParam(defaultValue="0") int page,
                                            @RequestParam(defaultValue="10") int size) {
        log.info("Listando itens - page={}, size={}", page, size);
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public ItemResponse getById(@PathVariable Long id) {
        log.info("Buscando item id={}", id);
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody ItemRequest request) {
        ItemResponse created = service.create(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @RequestBody ItemRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body(java.util.Map.of("id", id, "deleted", true, "message", "Item removido com sucesso"));
    }
}
