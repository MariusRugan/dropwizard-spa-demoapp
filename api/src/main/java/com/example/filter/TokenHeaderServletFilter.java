package com.example.filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

@WebFilter(filterName = "token-filter", urlPatterns = {"/api/*"})
public class TokenHeaderServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {

            String dateHeader = ((HttpServletRequest) request).getHeader(HttpHeaders.DATE);

            if (dateHeader != null) {
                chain.doFilter(request, response);
            } else {

                HttpServletResponse httpResponse = (HttpServletResponse) response;
                response.setCharacterEncoding("utf8");
                response.setContentType("application/json");
                httpResponse.setStatus(HttpStatus.BAD_REQUEST_400);

                Map<String, String> responseString = new LinkedHashMap<String, String>();
                responseString.put("value1", "label1");
                responseString.put("value2", "label2");
                responseString.put("value3", "label3");

                ObjectMapper mapper = new ObjectMapper();
                String jsonString;

                try {
                    jsonString = mapper.writeValueAsString(responseString);
                } catch (IOException e) {
                    jsonString = "fail";
                }

                httpResponse.getWriter().print(jsonString);
            }
        }
    }

    @Override
    public void destroy() {}
}