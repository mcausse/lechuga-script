package org.homs.lechugascript.tokenizer;

public class TokenAt {

    public static final TokenAt SYSTEM = new TokenAt("system", -1, -1);

    public final String sourceDesc;
    public final int row;
    public final int col;

    public TokenAt(String sourceDesc, int row, int col) {
        super();
        this.sourceDesc = sourceDesc;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return String.format("at %s:%s,%s", sourceDesc, row, col);
    }
}
