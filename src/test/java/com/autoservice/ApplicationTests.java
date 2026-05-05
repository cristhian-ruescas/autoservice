package com.autoservice;

import com.autoservice.config.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class ApplicationTests extends AbstractIntegrationTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void contextLoads() {
    }
}
