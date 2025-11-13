package com.claudiobianco.java.renderapi.web;

import com.claudiobianco.java.renderapi.web.dto.ItemRequest;
import com.claudiobianco.java.renderapi.web.dto.ItemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    String base() { return "http://localhost:" + port + "/api/v1/items"; }

    @Test
    void fluxoCompletoCRUD() {
        // CREATE
        ItemRequest req = new ItemRequest("Fonte", "Desc", "ATIVO", 450.0, "ELETR");
        ResponseEntity<ItemResponse> created = rest.postForEntity(base(), req, ItemResponse.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        Long id = created.getBody().id();

        // GET
        ResponseEntity<ItemResponse> got = rest.getForEntity(base()+"/"+id, ItemResponse.class);
        assertEquals(HttpStatus.OK, got.getStatusCode());
        assertEquals("Fonte", got.getBody().nome());

        // UPDATE
        ItemRequest upd = new ItemRequest("Fonte 700W", "Desc", "ATIVO", 520.0, "ELETR");
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ItemResponse> updated = rest.exchange(base()+"/"+id, HttpMethod.PUT, new HttpEntity<>(upd, h), ItemResponse.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals("Fonte 700W", updated.getBody().nome());

        // DELETE
        ResponseEntity<String> del = rest.exchange(base()+"/"+id, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.OK, del.getStatusCode());

        // GET 404
        ResponseEntity<String> missing = rest.getForEntity(base()+"/"+id, String.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
    }
}
