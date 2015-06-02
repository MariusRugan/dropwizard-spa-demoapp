package com.example.authentication;

import com.example.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by richard.tjerngren on 2015-01-14.
 */
public class MySecurityContext implements SecurityContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Optional<User> user;

    @Context
    HttpServletRequest webRequest;

    public MySecurityContext(final Optional<User> user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        logger.trace("getUserPrincipal called");
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        logger.trace("isUserinRole {} called", role);

        if (!user.isPresent()) {
            logger.debug("No user");
            return false;
        }

        if ("any".equals(role)) {
            logger.debug("Role is any, all logged in users are considered to be in this role");
            return true;
        }
        return user.get().getRole().equals(role);
    }

    @Override
    public boolean isSecure() {
        logger.trace("isSecure called");
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        logger.trace("getAuthScheme called");
        return null;
    }
}
