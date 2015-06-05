package com.example.authentication;


import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.example.exception.InvalidTokenException;
import com.example.exception.InvalidUserNameOrPasswordException;
import com.example.model.JWTToken;
import com.example.model.Token;
import com.example.persistence.TokenBlacklist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {

    public static final String SECRET = "secret_used_for_testing";
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final TokenBlacklist tokenBlackList;

    public AuthenticationManager(TokenBlacklist blacklist) {
        this.tokenBlackList = blacklist;
    }

    public Token login(final String username, final String password) throws InvalidUserNameOrPasswordException {
        if (!validateUserNamePassword(username, password)) {
            throw new InvalidUserNameOrPasswordException();
        }
        final String role = "admin";

        return generateToken(username, role);
    }

    private Token generateToken(String username, String role) {
        final JWTSigner signer = new JWTSigner(SECRET);

        Map<String, Object> claims = new HashMap<>();
        claims.put("user", username);
        claims.put("role", role);

        final JWTSigner.Options options = new JWTSigner.Options();
        options.setExpirySeconds(60 * 10);
        return new JWTToken(signer.sign(claims, options));
    }

    private boolean validateUserNamePassword(final String username, final String password) {
        return "username".equals(username) && "password".equals(password);
    }

    public boolean isValid(final Token token) {
        if (isTokenBlackListed(token)) {
            logger.debug("Token {} is blacklisted", token);
            return false;
        }
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        try {
            Map<String, Object> claims = verifier.verify(token.getRaw());
            if (!areClaimsValid(claims)) {
                //TODO better log message
                logger.debug("Token {} is not valid");
                return false;
            }

            return true;

        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException | SignatureException | JWTVerifyException e) {
            logger.error("{} exception when trying to verify token {}", e.getClass().getSimpleName(), token, e);
            return false;
        }
    }

    private boolean isTokenBlackListed(Token token) {
        return tokenBlackList.isBlackListed(token);
    }

    private boolean areClaimsValid(Map<String, Object> claims) {
        return claims.get("role").equals("admin");
    }

    public void logout(final Token token) {
        tokenBlackList.blacklist(token);
    }

    public Token getNewToken(Token token) throws InvalidTokenException {
        if (!isValid(token)) {
            throw new InvalidTokenException();
        }

        tokenBlackList.blacklist(token);

        //TODO this should probably somehow be accesible from the token
        Map<String, Object> claims;
        try {
            claims = new JWTVerifier(SECRET).verify(token.getRaw());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException | JWTVerifyException e) {
            //Token is already validated, this shouldn't happen
            logger.error("A valid token is no longer valid, expired?", e);
            throw new InvalidTokenException();
        }

        final String username = (String) claims.get("user");
        final String role = (String) claims.get("role");

        return generateToken(username, role);
    }

}
