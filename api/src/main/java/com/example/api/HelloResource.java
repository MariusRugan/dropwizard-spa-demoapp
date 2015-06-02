package com.example.api;

import com.google.common.base.Optional;

import com.example.model.Saying;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;


public class HelloResource implements HelloApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final AtomicLong initCounter = new AtomicLong();

    private final String template;

    private final String defaultName;

    private final AtomicLong counter;

    public HelloResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        logger.warn("HelloWorldResource number {} created", initCounter.incrementAndGet());
    }

    @Override
    public Saying sayHello(Optional<String> name) {
        logger.info("{} is using sayHello Api", name.or("Unknown"));
        final String value = String.format(template, name.or(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
