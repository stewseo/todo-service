package io.aklivity.zilla.example.todo.transport;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.AbstractJsonProvider;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.composite.loggingevent.LoggingEventNestedJsonProvider;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.junit.Before;
import org.slf4j.LoggerFactory;


import java.io.InputStream;
import java.net.InetSocketAddress;

public class CustomLogstashAppender extends AbstractJsonProvider {
    Logger logger;
    @Before
    void before(){
        logger = createLoggerFor("customJsonAppender","stash.log");
    }
    private static Logger createLoggerFor(String string, String file) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(string);
        logger.addAppender(fileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        return logger;
    }

    @Override
    public Object parse(String s) throws InvalidJsonException
    {
        logger.info("parse {}" , s);
        return null;
    }

    @Override
    public Object parse(InputStream inputStream, String s) throws InvalidJsonException
    {
        logger.info("parse {}" , s);
        return null;
    }

    @Override
    public String toJson(Object o)
    {
        logger.info("parse {}", o);
        return null;
    }

    @Override
    public Object createArray()
    {
        return null;
    }

    @Override
    public Object createMap()
    {
        logger.info("");
        return null;
    }
}
