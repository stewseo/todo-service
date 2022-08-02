/*
 * Copyright 2021-2022 Aklivity. All rights reserved.
 */

package io.aklivity.zilla.example.todo;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;


@SuppressWarnings({"RedundantCast", "RedundantThrows"})
public class KafkaConfigurationTest {

    private static final DockerImageName KAFKA_TEST_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.2.1");

    @Test
    public void bitnamiKafkaContainerTest() {
        GenericContainer<?> kafkaContainer = new GenericContainer<>(KAFKA_TEST_IMAGE)
                .withExposedPorts(9092)
                .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.kafka")))
                .withCreateContainerCmdModifier((Consumer<CreateContainerCmd>) cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(9092), new ExposedPort(9092)))
                ))
                .withEnv("ALLOW_PLAINTEXT_LISTENER", "true")
                .withEnv("KAFKA_CFG_NODE_ID", "1")
                .withEnv("KAFKA_CFG_BROKER_ID", "1")
                .withEnv("KAFKA_CFG_CONTROLLER_QUORUM_VOTERS", "1@127.0.0.1:9093")
                .withEnv("KAFKA_CFG_PROCESS_ROLES", "broker,controller")
                .withEnv("KAFKA_CFG_ADVERTISED_LISTENERS", "CLIENT://:9092,INTERNAL://:29092,CONTROLLER://:9093")
                .withCommand("run", "-e}")
                .withReuse(true);

        assertEquals(6, kafkaContainer.getEnv().size());
    }

    @Test
    public void addKafkaAsServiceTest() {
        DockerComposeContainer<?> kafkaContainer =
                new DockerComposeContainer<>(new File("src/test/resources/kafka.yml"))
                        .withExposedService("kafka_1", 29092);
    }


}
