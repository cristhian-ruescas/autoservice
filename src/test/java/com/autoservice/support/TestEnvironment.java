package com.autoservice.support;

import org.testcontainers.DockerClientFactory;

public final class TestEnvironment {

    private TestEnvironment() {
    }

    public static boolean dockerAvailable() {
        return DockerClientFactory.instance().isDockerAvailable();
    }
}
