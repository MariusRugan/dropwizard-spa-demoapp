package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO REMOVE ME
 */
public class JWTToken extends Token
{

	@JsonCreator
	public JWTToken(@JsonProperty("raw") String base64)
	{
		super(base64);
	}

}
