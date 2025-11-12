package com.claudiobianco.java.renderapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RenderApiApplicationTests {

    @Test
    void contextLoads() {
        // Apenas garante que o contexto sobe sem erro
    }

    @Test
    void mainDeveRodarSemErros() {
        RenderApiApplication.main(new String[]{});
    }
}