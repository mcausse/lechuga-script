package org.homs.lechugascript.tokenizer;

public class Token {
    public final EToken type;
    public final String value;
    public final TokenAt tokenAt;

    public Token(EToken type, String value, String sourceDesc, int row, int col) {
        this.type = type;
        this.value = value;
        this.tokenAt = new TokenAt(sourceDesc, row, col);
    }

    public Token(EToken type, String value, TokenAt tokenAt) {
        super();
        this.type = type;
        this.value = value;
        this.tokenAt = tokenAt;
    }

    @Override
    public String toString() {
        return type.name() + ":" + value;
    }
}