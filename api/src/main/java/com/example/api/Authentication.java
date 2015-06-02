package com.example.api;

import com.example.model.Token;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
@DenyAll
public interface Authentication {

    @GET
    @PermitAll
    Token login(@QueryParam("username") String username, @QueryParam("password") String password);

    @GET
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"any"})
    Token refreshToken();

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"any"})
    void logout();
}
