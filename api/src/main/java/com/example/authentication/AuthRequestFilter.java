package com.example.authentication;

import com.auth0.jwt.JWTVerifier;
import com.example.model.User;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.servlet.ServletException;
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

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /*
  @Context
  HttpServletRequest webRequest;
  */

  public AuthRequestFilter() {
    logger.info("Constructor called");
    jwtVerifier = new JWTVerifier(new Base64(true).decodeBase64("9A5qiAy8qqw1BuGsu3tpqpOelvecWesPLs07DkZAxiONt2J1_3YJWF392s5Q_AnQ"), "0FmFogYWO21GXbwjUcZ4D7Nsuklvosbk");
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    logger.trace("filter called");

    Optional<String> encodedToken = null;
    try {
      encodedToken = Optional.of(getToken(requestContext));
    } catch (Exception e) {}

    logger.trace("token header is: {}", encodedToken.orElse("not present"));

    if (!encodedToken.isPresent()) {
      requestContext.setSecurityContext(new MySecurityContext(Optional.<User>empty()));
      return;
    }

    final Optional<User> user;
    user = getUser(encodedToken.get());

    if (!user.isPresent()) {
      final Response.Status unauthorized = Response.Status.UNAUTHORIZED;
      requestContext.abortWith(Response.status(unauthorized).build());
      return;
    }

    requestContext.setSecurityContext(new MySecurityContext(user));
  }

  private String getToken(ContainerRequestContext requestContext) throws ServletException {

    String token = null;

    final String authorizationHeader = requestContext.getHeaderString("authorization");

    if (authorizationHeader == null) {
      throw new ServletException("Unauthorized: No Authorization header was found");
    }

    String[] parts = authorizationHeader.split(" ");
    if (parts.length != 2) {
      throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
    }

    String scheme = parts[0];
    String credentials = parts[1];

    Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
    if (pattern.matcher(scheme).matches()) {
      token = credentials;
    }
    return token;
  }

  private Optional<User> getUser(String encodedToken) {
    /**
     * TODO use injected authentication manager instead
     * and make sure this token is not blacklisted
     */
    final Map<String, Object> decoded;
    try {
      decoded = jwtVerifier.verify(encodedToken);
      logger.info("Token valid");
    } catch (Exception e) {
      logger.warn("Token invalid, wont add any user to request");
      return Optional.empty();
    }

    return Optional.of(new User((String) decoded.get("user"), (String) decoded.get("role")));
  }
}