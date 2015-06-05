package com.example.api;

import com.google.common.base.Optional;

import com.codahale.metrics.annotation.Timed;
import com.example.model.Saying;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/hello")
@DenyAll
@Produces(MediaType.APPLICATION_JSON)
public interface HelloApi {

    @GET
    @PermitAll
    @Timed
    Saying getHello(@QueryParam("name") Optional<String> name);
}
