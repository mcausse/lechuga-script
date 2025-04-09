package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

public class StringAst extends Ast {

    final String value;

    public StringAst(Position tokenPosition, String value) {
        super(tokenPosition);
        this.value = value;
    }

    @Override
    public Object evaluate(Environment env) {
        return value;
    }
}
