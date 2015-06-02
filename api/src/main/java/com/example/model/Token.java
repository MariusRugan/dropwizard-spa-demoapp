package com.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Token
{
	protected final String raw;

	@JsonCreator
	public Token(@JsonProperty("raw") String base64)
	{
		this.raw = base64;
	}

	@JsonIgnore
	@Override
	public String toString()
	{
		return "JWTToken{" +
				"raw='" + raw + '\'' +
				'}';
	}

	@JsonProperty
	public String getRaw()
	{
		return raw;
	}

	@JsonIgnore
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JWTToken jwtToken = (JWTToken) o;

        return raw.equals(jwtToken.raw);

    }

	@JsonIgnore
	@Override
	public int hashCode()
	{
		return raw.hashCode();
	}
}
