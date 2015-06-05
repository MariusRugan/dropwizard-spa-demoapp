package com.example.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class HelloSecureResource implements HelloSecureApi {

    private static final AtomicLong initCounter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Context
    private SecurityContext securityContext;

    public HelloSecureResource() {
        logger.info("{} number {} created", getClass().getSimpleName(), initCounter.incrementAndGet());
    }

    @Override
    public String getHelloSecure() {

        logger.info("{}.test called", getClass().getSimpleName());
        logger.info("SecurityContext is {}", securityContext.getClass().getSimpleName());

        return "OK";
    }

}
