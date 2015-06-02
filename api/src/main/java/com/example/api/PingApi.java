package com.example.api;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;

import com.example.model.Saying;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
public interface PingApi
{
  @GET
  @Timed
  Saying sayHello(@QueryParam("name") Optional<String> name);
}