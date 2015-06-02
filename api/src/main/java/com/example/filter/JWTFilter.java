package com.example.filter;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTVerifier;

@WebFilter(filterName= "jwt-filter", urlPatterns = { "/api/*" })
public class JWTFilter implements Filter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private JWTVerifier jwtVerifier;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    jwtVerifier = new JWTVerifier(
        new Base64(true).decodeBase64("9A5qiAy8qqw1BuGsu3tpqpOelvecWesPLs07DkZAxiONt2J1_3YJWF392s5Q_AnQ"), "0FmFogYWO21GXbwjUcZ4D7Nsuklvosbk"
    );
  }

  /**
   *
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    String token = getToken((HttpServletRequest) request);

    try {
      Map<String, Object> decoded = jwtVerifier.verify(token);
      // Do something with decoded information like UserId
      chain.doFilter(request, response);

    } catch (Exception e) {
      throw new ServletException("Unauthorized: Token validation failed", e);
    }

  }

  /**
   *
   * @param httpRequest
   * @return
   * @throws ServletException
   */
  private String getToken(HttpServletRequest httpRequest) throws ServletException {

    String token = null;
    final String authorizationHeader = httpRequest.getHeader("authorization");
    if (authorizationHeader == null) {
      throw new WebApplicationException("Unauthorized: No Authorization header was found");
    }

    String[] parts = authorizationHeader.split(" ");
    if (parts.length != 2) {
      throw new WebApplicationException("Unauthorized: Format is Authorization: Bearer [token]");
    }

    String scheme = parts[0];
    String credentials = parts[1];

    Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
    if (pattern.matcher(scheme).matches()) {
      token = credentials;
    }

    return token;
  }

  @Override
  public void destroy() {}

}