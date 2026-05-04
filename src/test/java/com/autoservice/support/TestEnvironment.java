package com.autoservice.support;

import org.testcontainers.DockerClientFactory;

public final class TestEnvironment {

    private TestEnvironment() {
    }

    /**
     * Usado por {@link org.junit.jupiter.api.condition.EnabledIf} nos testes de integração com Testcontainers.
     */
    public static boolean dockerAvailable() {
        return DockerClientFactory.instance().isDockerAvailable();
    }
}
