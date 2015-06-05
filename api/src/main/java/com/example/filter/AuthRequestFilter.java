package com.example.filter;

import com.auth0.jwt.JWTVerifier;
import com.example.exception.NotAuthorizedException;
import com.example.authentication.DemoAppSecurityContext;
import com.example.model.User;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class AuthRequestFilter implements ContainerRequestFilter {

    private JWTVerifier jwtVerifier;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /*
    @Context
    HttpServletRequest webRequest;
    */

    public AuthRequestFilter() {
        jwtVerifier = new JWTVerifier(
            new Base64(true).decodeBase64("9A5qiAy8qqw1BuGsu3tpqpOelvecWesPLs07DkZAxiONt2J1_3YJWF392s5Q_AnQ"),
            "0FmFogYWO21GXbwjUcZ4D7Nsuklvosbk"
        );
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final Optional<String> encodedToken = getToken(requestContext);

        if (!encodedToken.isPresent()) {
            requestContext.setSecurityContext(new DemoAppSecurityContext(Optional.<User>empty()));
            return;
        }

        final Optional<User> user;
        user = getUser(encodedToken.get());

        if (!user.isPresent()) {
            final Response.Status unauthorized = Response.Status.UNAUTHORIZED;
            requestContext.abortWith(Response.status(unauthorized).build());
            return;
        }

        requestContext.setSecurityContext(new DemoAppSecurityContext(user));
    }

    /**
     *
     * @param requestContext
     * @return
     * @throws NotAuthorizedException
     */
    private Optional<String> getToken(ContainerRequestContext requestContext) throws NotAuthorizedException {

        String token = null;

        final String authorizationHeader = requestContext.getHeaderString("authorization");

        if (authorizationHeader == null) {
            throw new NotAuthorizedException("Unauthorized: No Authorization header was found");
        }

        String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2) {
            throw new NotAuthorizedException("Unauthorized: Format is Authorization: Bearer [token]");
        }

        String scheme = parts[0];
        String credentials = parts[1];

        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(scheme).matches()) {
            token = credentials;
        }

        return Optional.ofNullable(token);
    }

    /**
     * @todo use injected authentication manager instead and make sure this token is not blacklisted
     * @param encodedToken
     * @return
     */
    private Optional<User> getUser(String encodedToken) {

        final Map<String, Object> decoded;

        try {
            decoded = jwtVerifier.verify(encodedToken);
            LOGGER.trace("Token valid");
        } catch (Exception e) {
            LOGGER.warn("Token invalid, won't add any user to request");
            return Optional.empty();
        }

        return Optional.of(new User((String) decoded.get("user"), (String) decoded.get("role")));
    }
}