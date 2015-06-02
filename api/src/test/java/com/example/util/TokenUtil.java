package com.example.util;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.example.model.JWTToken;
import com.example.authentication.AuthenticationManager;
import com.example.model.Token;
import org.junit.Assert;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

public class TokenUtil
{
	/**
	 * Normal token except the secret is the_wrong_secret and it has no expiration date
	 */
	public static final JWTToken
	    JWT_TOKEN_WITH_THE_WRONG_SECRET = new JWTToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidXNlcm5hbWUiLCJyb2xlIjoiYWRtaW4ifQ.FR8YuIxjfqE5_s_6cHYGlRl2CE7lnt7s8WSQSbeMlC4");

	/**
	 * Normal token except it has no expiration date, the secret is secret_used_for_testing
	 */
	public static final JWTToken VALID_JWT_TOKEN = new JWTToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidXNlcm5hbWUiLCJyb2xlIjoiYWRtaW4ifQ.4A96ZeAOjDVeDjgJ5jPBYz9uZkPKymXCEalU-fy1naI");

	/**
	 * Normal token exect it has expired (unless your system clock is bonkers, the secret is secret_used_for_testing
	 */
	public static final JWTToken VALID_BUT_EXPIRED_JWT_TOKEN = new JWTToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0MjE0MTEzMjUsInVzZXIiOiJ1c2VybmFtZSIsInJvbGUiOiJhZG1pbiJ9.1hoz_bQB5dh4axtStwXLPR6UDLU_dzxsrzcVJuEQ05c");


	public static void validateToken(Token token) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, JWTVerifyException
	{
		final JWTVerifier verifier = new JWTVerifier(AuthenticationManager.SECRET);
		Map<String, Object> claims = verifier.verify(token.getRaw());
		Assert.assertEquals("Wrong username", "username", claims.get("user"));
	}
}
