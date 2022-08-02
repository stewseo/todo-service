/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */

package io.aklivity.zilla.example.todo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class ServiceContainerTest {

    private static final DockerImageName TCP_ECHO_SERVER = DockerImageName.parse("src/test/resources/kafka.yml");
    @Container
    static GenericContainer<?> container = new GenericContainer<>(
            TCP_ECHO_SERVER
    )
            .withCommand("run -v zilla.json:/zilla.json ghcr.io/aklivity/zilla:latest");

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        container.start();
        assertTrue(container.isRunning());

        String containerId = container.getContainerId();
        org.testcontainers.containers.Container.ExecResult inspect = container.execInContainer("inspect", containerId);

        assertEquals(126, inspect.getExitCode());
    }

    @AfterAll
    static void cleanup() throws IOException, InterruptedException {
        container.close();
    }
}
