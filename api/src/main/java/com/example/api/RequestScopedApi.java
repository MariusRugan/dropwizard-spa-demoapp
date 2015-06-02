package com.example.api;

import com.codahale.metrics.annotation.Timed;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/request")
@Produces(MediaType.APPLICATION_JSON)
public interface RequestScopedApi
{
	@RolesAllowed("admin")
	@GET
	@Timed
	String test();
}
