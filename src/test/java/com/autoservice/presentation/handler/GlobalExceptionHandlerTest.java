package com.autoservice.presentation.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FailureController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @CsvSource({"authentication,401", "authorization,403", "unexpected,500"})
    void springInvocaHandlerAnotado(final String failure, final int expectedStatus) throws Exception {
        mockMvc.perform(get("/handler-test/" + failure).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
    }

    @Test
    void jsonInvalidoRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/handler-test/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/handler-test/json"));
    }

    @RestController
    static class FailureController {

        @GetMapping("/handler-test/{failure}")
        public void fail(@PathVariable final String failure) {
            switch (failure) {
                case "authentication" -> throw new BadCredentialsException("Invalid credentials");
                case "authorization" -> throw new AccessDeniedException("Access denied");
                default -> throw new IllegalStateException("Unexpected failure");
            }
        }

        @PostMapping("/handler-test/json")
        public Map<String, String> json(@RequestBody final Map<String, String> body) {
            return body;
        }
    }
}
