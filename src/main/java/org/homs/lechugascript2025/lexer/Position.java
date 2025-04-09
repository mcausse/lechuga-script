package org.homs.lechugascript2025.lexer;

public class Position {

    public final String templateUrn;
    public final int row;
    public final int col;

    public Position(String templateUrn, int row, int col) {
        this.templateUrn = templateUrn;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return templateUrn + ":" + row + "," + col;
    }
}
