package org.homs.lechugascript2025.parser;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;

public abstract class Ast {

    final Position tokenPosition;

    public Ast(Position tokenPosition) {
        this.tokenPosition = tokenPosition;
    }

    public Position getTokenPosition() {
        return tokenPosition;
    }

    public abstract Object evaluate(Environment env);
}

