package com.example.persistence;

import com.example.model.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Since JWT tokens are self-validated, in order to prevent undesirable tokens (most often logged out)
 * from authenticating we need to add them to a blacklist until they expire
 * TODO add expiration so that this set doesn't indefinitely
 */
public class SimpleSetBlacklist implements TokenBlacklist
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Set<Token> tokenSet;

	public SimpleSetBlacklist()
	{
		tokenSet = new HashSet<>();
	}

	@Override
	public void blacklist(Token token)
	{
		logger.trace("Blacklisted {}", token);
		tokenSet.add(token);
	}

	@Override
	public boolean isBlackListed(Token token)
	{
		return tokenSet.contains(token);
	}

	public void clearBlackList()
	{
		tokenSet.clear();
	}

	private static class InstanceHolder
	{
		private static final SimpleSetBlacklist INSTANCE = new SimpleSetBlacklist();
	}

	public static SimpleSetBlacklist getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
}
