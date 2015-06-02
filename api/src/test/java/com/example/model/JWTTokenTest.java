package com.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

public class JWTTokenTest
{
	@Test
	public void testConvertFromTokenAndBack() throws Exception
	{
		final Token token = new JWTToken("raw-data");

		final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		final String json = ow.writeValueAsString(token);
		System.out.println(json);

		final Token parsedToken = new ObjectMapper().readValue(json, JWTToken.class);

		System.out.println(parsedToken);
	}
}