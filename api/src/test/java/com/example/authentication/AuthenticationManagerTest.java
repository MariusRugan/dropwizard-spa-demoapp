package com.example.authentication;

import com.auth0.jwt.JWTVerifyException;
import com.example.DemoApp;
import com.example.DemoAppConfiguration;
import com.example.api.Authentication;
import com.example.model.Token;
import com.example.util.TokenUtil;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.example.persistence.SimpleSetBlacklist;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.LinkedList;
import java.util.List;

public class AuthenticationManagerTest
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@ClassRule
 	public static final DropwizardAppRule<DemoAppConfiguration> RULE =
         new DropwizardAppRule<>(DemoApp.class, ResourceHelpers.resourceFilePath("hello-world.yml"));

	private Authentication api;

	@Before
	public void setUp() throws Exception
	{
		final List<JacksonJsonProvider> providers = new LinkedList<>();
		providers.add(new JacksonJsonProvider());
		final String hostname = "localhost";
		final int port = 8080;

		api = JAXRSClientFactory.create("http://" + hostname + ":" + port + "/api", Authentication.class, providers);
	}

	@After
	public void tearDown() throws Exception
	{
		api = null;
		//TODO remove this after dependency injection works
		SimpleSetBlacklist.getInstance().clearBlackList();
	}

	@Test
	public void testLoginWithValidCredentialsShouldReturnAValidToken() throws Exception
	{
		final Token token = login();
	}

	private Token login() throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, JWTVerifyException
	{
		final Token token = api.login("username", "password");
		Assert.assertNotNull("Token shouldn't be null", token);
		TokenUtil.validateToken(token);
		return token;
	}

	@Test
	public void testRefreshTokenShouldReturnAnewToken() throws Exception
	{
		final Token token = login();

		WebClient.client(api).header("x-token-jwt", token.getRaw());

		final Token newToken = api.refreshToken();
	}

	@Test
	public void testRefreshTokenWithAnInvalidShouldNotReturnAToken() throws Exception
	{
		try
		{
			final Token newToken = api.refreshToken();
			Assert.fail("Expected an exception");
		}
		catch (ForbiddenException e)
		{
			Assert.assertEquals("Wrong exception message", "HTTP 403 Forbidden", e.getMessage());
		}
	}

	@Test(expected = BadRequestException.class)
	public void testLoginWithInvalidCredentialsShouldFail() throws Exception
	{
		final Token token = api.login("qweqweqe", "asdasd");
	}

	@Test(expected = BadRequestException.class)
	public void testTestRefreshTokenWithAnExpiredTokenShouldNotWork() throws Exception
	{
		final Token token = login();

		WebClient.client(api).header("x-token-jwt", token.getRaw());

		final Token newToken = api.refreshToken();
		final Token newerToken = api.refreshToken();
	}

	@Test
	public void testLogoutShouldInvalidateToken() throws Exception
	{
		final Token token = login();
		WebClient.client(api).header("x-token-jwt", token.getRaw());
		api.logout();
		try
		{
			final Token newToken = api.refreshToken();
			Assert.fail("Expected an exception");
		}
		catch (BadRequestException e)
		{
			Assert.assertEquals("Wrong exception message", "HTTP 400 Bad Request", e.getMessage());
		}
	}
}
