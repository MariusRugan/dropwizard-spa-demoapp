package com.example.filter;

import com.auth0.jwt.JWTVerifier;
import com.example.exception.NotAuthorizedException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

@WebFilter(filterName = "jwt-filter", urlPatterns = {"/api/*"})
public class JwtServletFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JWTVerifier jwtVerifier;

    @Override
    public void init(FilterConfig filterConfig) throws WebApplicationException {
        jwtVerifier = new JWTVerifier(
            new Base64(true).decodeBase64("9A5qiAy8qqw1BuGsu3tpqpOelvecWesPLs07DkZAxiONt2J1_3YJWF392s5Q_AnQ"),
            "0FmFogYWO21GXbwjUcZ4D7Nsuklvosbk"
        );
    }

    /**
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws WebApplicationException, IOException {

        try {

            String token = getToken((HttpServletRequest) request, (HttpServletResponse) response);
            Map<String, Object> decoded = jwtVerifier.verify(token);
            chain.doFilter(request, response);

        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(401);
            httpResponse.getWriter().print(e.getMessage());
        }

    }

    /**
     * @todo: investigate
     */
    private String getToken(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) throws WebApplicationException, IOException {

        String token = null;
        final String authorizationHeader = httpRequest.getHeader("authorization");
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

        return token;
    }

    @Override
    public void destroy() {}

}