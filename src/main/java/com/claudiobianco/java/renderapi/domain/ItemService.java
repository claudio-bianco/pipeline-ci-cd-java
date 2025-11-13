package com.claudiobianco.java.renderapi.domain;

import org.springframework.stereotype.Service;

import com.claudiobianco.java.renderapi.web.dto.ItemRequest;
import com.claudiobianco.java.renderapi.web.dto.ItemResponse;
import com.claudiobianco.java.renderapi.web.dto.PagedResponse;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {

    private final Map<Long, ItemResponse> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public PagedResponse<ItemResponse> list(int page, int size) {
        List<ItemResponse> all = new ArrayList<>(store.values());
        all.sort(Comparator.comparingLong(ItemResponse::id));
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        List<ItemResponse> slice = all.subList(from, to);
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) all.size() / size);

        return new PagedResponse<>(slice, page, size, all.size(), totalPages);
    }

    public ItemResponse get(Long id) {
        ItemResponse item = store.get(id);
        if (item == null) throw new ItemNotFoundException(id);
        return item;
    }

    public ItemResponse create(ItemRequest req) {
        long id = idGenerator.incrementAndGet();
        String now = Instant.now().toString();
        ItemResponse item = new ItemResponse(id, req.nome(), req.descricao(), req.status(), req.preco(), req.categoria(), now, now);
        store.put(id, item);
        return item;
    }

    public ItemResponse update(Long id, ItemRequest req) {
        ItemResponse current = store.get(id);
        if (current == null) throw new ItemNotFoundException(id);
        String now = Instant.now().toString();
        ItemResponse updated = new ItemResponse(
                id, req.nome(), req.descricao(), req.status(), req.preco(), req.categoria(),
                current.createdAt(), now
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        ItemResponse removed = store.remove(id);
        if (removed == null) throw new ItemNotFoundException(id);
    }
}
