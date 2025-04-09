package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

public class NullAst extends Ast {

    public NullAst(Position tokenPosition) {
        super(tokenPosition);
    }

    @Override
    public Object evaluate(Environment env) {
        return null;
    }
}


