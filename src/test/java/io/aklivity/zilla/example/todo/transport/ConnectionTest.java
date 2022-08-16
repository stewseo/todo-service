package io.aklivity.zilla.example.todo.transport;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.*;
import net.logstash.logback.composite.loggingevent.*;
import net.logstash.logback.fieldnames.LogstashFieldNames;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;

import java.io.IOException;


public class ConnectionTest extends AbstractFieldJsonProvider<ILoggingEvent>
        implements FieldNamesAware<LogstashFieldNames> {
    public static final String FIELD_MESSAGE = "jsonmessage";

    public ConnectionTest() {
        setFieldName(FIELD_MESSAGE);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        System.out.println("Sawyer" + event.getFormattedMessage());
        JsonWritingUtils.writeStringField(generator, getFieldName(), event.getFormattedMessage());
    }

    @Override
    public void setFieldNames(LogstashFieldNames fieldNames) {
        setFieldName(fieldNames.getMessage());
    }

    public static class RestHighLevelClient { }

    public static class RestHighLevelClientBuilder {
        public RestHighLevelClientBuilder(RestClient restClient) {
        }
        public RestHighLevelClientBuilder setApiCompatibilityMode(Boolean enabled) {
            return this;
        }

        public RestHighLevelClient build() {
            return new RestHighLevelClient();
        }
    }

    @Test
    public void migrate() {

        RestClient httpClient = RestClient.builder(
                new HttpHost("localhost", 9200)
        ).build();

        RestHighLevelClient hlrc = new RestHighLevelClientBuilder(httpClient)
                .setApiCompatibilityMode(true) // <1>
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                httpClient,
                new JacksonJsonpMapper()
        );

        ElasticsearchClient esClient = new ElasticsearchClient(transport);
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void addStatus(Status status) {

    }

    @Override
    public void addInfo(String s) {

    }

    @Override
    public void addInfo(String s, Throwable throwable) {

    }

    @Override
    public void addWarn(String s) {

    }

    @Override
    public void addWarn(String s, Throwable throwable) {

    }

    @Override
    public void addError(String s) {

    }

    @Override
    public void addError(String s, Throwable throwable) {
    }
}
