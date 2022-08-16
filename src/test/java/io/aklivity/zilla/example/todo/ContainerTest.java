/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */

package io.aklivity.zilla.example.todo;

import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ContainerTest {

    private static final int KAFKA_PORT = 9093;
    private static final int INIT_KAFKA_PORT = 8080;
    private static final int ZILLA_PORT = 8080;

    @Container
    DockerComposeContainer<?> dockerComposeContainer =
            new DockerComposeContainer<>(new File("src\\test\\resources\\kafka-init.yml"))
                    .withExposedService("kafka", KAFKA_PORT)
                    .withExposedService("init-kafka", INIT_KAFKA_PORT)
                    .withExposedService("zilla", ZILLA_PORT)
                    .withLocalCompose(true);

    @Test
    void kafkaTest() throws IOException, InterruptedException {
        org.testcontainers.Testcontainers.exposeHostPorts(8080);
        int kafkaPort = dockerComposeContainer.getServicePort("kafka", KAFKA_PORT);

        String kafkaUrl = dockerComposeContainer.getServiceHost("kafka", KAFKA_PORT)
                + ":" +
                kafkaPort;

        assertEquals("localhost:"+kafkaPort, kafkaUrl);

    }

    @Test
    void zillaTest() throws IOException, InterruptedException {
        int zillaPort = dockerComposeContainer.getServicePort("zilla", ZILLA_PORT);

        String zillaUrl = dockerComposeContainer.getServiceHost("zilla", ZILLA_PORT)
                + ":" +
                zillaPort;

        assertEquals("localhost:"+zillaPort, zillaUrl);
    }

    @Test
    void initKafkaTest() throws IOException, InterruptedException {
        int initKafkaPort = dockerComposeContainer.getServicePort("init-kafka", INIT_KAFKA_PORT);

        String initKafkaUrl = dockerComposeContainer.getServiceHost("init-kafka", INIT_KAFKA_PORT)
                + ":" +
                initKafkaPort;

        assertEquals("localhost:"+initKafkaPort, initKafkaUrl);
    }

}







