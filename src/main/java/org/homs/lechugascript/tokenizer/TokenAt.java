package org.homs.lechugascript.tokenizer;

import java.util.Objects;

public class TokenAt implements Comparable<TokenAt> {

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
    public int compareTo(TokenAt o) {
        int r = sourceDesc.compareTo(o.sourceDesc);
        if (r == 0) {
            r = Integer.compare(row, o.row);
            if (r == 0) {
                r = Integer.compare(col, o.col);
            }
        }
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenAt tokenAt = (TokenAt) o;
        return row == tokenAt.row && col == tokenAt.col && sourceDesc.equals(tokenAt.sourceDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceDesc, row, col);
    }

    @Override
    public String toString() {
        return String.format("at %s:%s,%s", sourceDesc, row, col);
    }

}
