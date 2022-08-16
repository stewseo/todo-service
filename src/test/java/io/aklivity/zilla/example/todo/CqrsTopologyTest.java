package io.aklivity.zilla.example.todo;

import io.aklivity.zilla.example.todo.model.*;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CqrsTopologyTest {

    private static final String TASK_COMMANDS_TOPIC = "task-commands";
    private static final String TASK_SNAPSHOTS_TOPIC = "task-snapshots";
    private static final String TASK_REPLIES_TOPIC = "task-replies";
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, Command> commandsInTopic;
    private TestOutputTopic<String, Task> snapshotsOutTopic;
    private TestOutputTopic<String, Object> commandsResponseTopic;

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @BeforeEach
    public void setUp()
    {
        final StreamsBuilder builder = new StreamsBuilder();
        final CqrsTopology processor = new CqrsTopology();
        processor.taskCommandsTopic = TASK_COMMANDS_TOPIC;
        processor.taskSnapshotsTopic = TASK_SNAPSHOTS_TOPIC;
        processor.taskRepliesTopic = TASK_REPLIES_TOPIC;
        processor.buildPipeline(builder);
        final org.apache.kafka.streams.Topology topology = builder.build();

        final Properties props = new Properties();
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        testDriver = new TopologyTestDriver(topology, props);

        commandsInTopic = testDriver.createInputTopic(TASK_COMMANDS_TOPIC,
                new StringSerializer(), new JsonSerializer<>());
        StringDeserializer keyDeserializer = new StringDeserializer();
        KafkaJsonDeserializer<Task> snapshotDeserializer = new KafkaJsonDeserializer<Task>();
        snapshotDeserializer.configure(Collections.emptyMap(), false);
        snapshotsOutTopic = testDriver.createOutputTopic(TASK_SNAPSHOTS_TOPIC,
                keyDeserializer, snapshotDeserializer);
        KafkaJsonDeserializer<Object> responseDeserializer = new KafkaJsonDeserializer<>();
        responseDeserializer.configure(Collections.emptyMap(), false);
        commandsResponseTopic = testDriver.createOutputTopic(TASK_REPLIES_TOPIC,
                keyDeserializer, responseDeserializer);
    }

    @Test
    public void shouldProcessCreateTaskCommand()
    {
        final Headers headers = new RecordHeaders(
                new Header[]{
                        new RecordHeader("zilla:domain-model", "CreateTaskCommand".getBytes()),
                        new RecordHeader("zilla:correlation-id", "1".getBytes()),
                        new RecordHeader("idempotency-key", "task1".getBytes()),
                        new RecordHeader(":path", "/task".getBytes())
                });
        commandsInTopic.pipeInput(new TestRecord<>("task1", CreateTaskCommand.builder()
                .name("Test")
                .build(), headers));
        List<KeyValue<String, Object>> response = commandsResponseTopic.readKeyValuesToList();
        assertEquals(1, response.size());
        List<KeyValue<String, Task>> snapshots = snapshotsOutTopic.readKeyValuesToList();
        assertEquals(1, snapshots.size());
    }

    @Test
    public void shouldProcessUpdateTaskCommand()
    {
        final Headers createHeaders = new RecordHeaders(
                new Header[]{
                        new RecordHeader("zilla:domain-model", "CreateTaskCommand".getBytes()),
                        new RecordHeader("zilla:correlation-id", "1".getBytes()),
                        new RecordHeader("idempotency-key", "task1".getBytes()),
                        new RecordHeader(":path", "/task".getBytes())
                });
        commandsInTopic.pipeInput(new TestRecord<>("task1", CreateTaskCommand.builder()
                .name("Test")
                .build(), createHeaders));
        final Headers headers = new RecordHeaders(
                new Header[]{
                        new RecordHeader("zilla:domain-model", "RenameTaskCommand".getBytes()),
                        new RecordHeader("zilla:correlation-id", "1".getBytes()),
                        new RecordHeader("idempotency-key", "task1".getBytes()),
                        new RecordHeader(":path", "/task".getBytes())
                });
        commandsInTopic.pipeInput(new TestRecord<>("task1", RenameTaskCommand.builder()
                .name("Test")
                .build(), headers));
        List<KeyValue<String, Object>> response = commandsResponseTopic.readKeyValuesToList();
        assertEquals(2, response.size());
        List<KeyValue<String, Task>> snapshots = snapshotsOutTopic.readKeyValuesToList();
        assertEquals(2, snapshots.size());
    }
    @Test
    void buildPipeline() {
    }


}