package com.claudiobianco.java.renderapi.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HelloControllerTests {

    @Test
    void deveRetornarHelloWorld() throws Exception {
        HelloController controller = new HelloController();

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        mockMvc.perform(get("/api/v1/hello")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}