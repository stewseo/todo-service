/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */
package io.aklivity.zilla.example.todo;

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;


public class ServiceConfigurationTest {

    public void addSpringBootAsServiceTest() {
        try (DockerComposeContainer<?> toDoService = new DockerComposeContainer<>(new File("resources/to-do.yml"))
        ) {

        }
    }

}
