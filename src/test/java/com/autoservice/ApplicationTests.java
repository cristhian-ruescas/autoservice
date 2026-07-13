package com.autoservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class ApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }
}
