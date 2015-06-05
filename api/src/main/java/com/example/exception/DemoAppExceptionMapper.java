package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * inspired by
 *
 * http://nbsoftsolutions.com/blog/writing-a-dropwizard-json-app
 * http://gary-rowe.com/agilestack/2012/10/23/how-to-implement-a-runtimeexceptionmapper-for-dropwizard/
 *
 * https://github.com/stevenalexander/dropwizard-handle-exceptions
 * http://stackoverflow.com/questions/15225436/dropwizard-filter-gives-me-500-server-error-instead-of-the-response-status-def
 *
 */
@Provider
public class DemoAppExceptionMapper implements ExceptionMapper<RuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoAppExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException e) {
        LOGGER.error("API invocation failed. Runtime : {}, Message : {}", e, e.getMessage());
        return Response.serverError().type(MediaType.APPLICATION_JSON).entity(new Error()).build();
    }
}