package com.example.persistence;

import com.example.model.Token;

public interface TokenBlacklist {

    void blacklist(Token token);

    boolean isBlackListed(Token token);
}
