package com.example.wehealed.medical_lens;

public class TokenResponseJSON {
    Token[] tokens;

    public TokenResponseJSON(Token[] tokens) {
        this.tokens = tokens;
    }

    public Token[] getTokens() {
        return tokens;
    }

    public void setTokens(Token[] tokens) {
        this.tokens = tokens;
    }
}
