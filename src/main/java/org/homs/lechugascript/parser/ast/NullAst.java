package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;

public class NullAst extends Ast {

    public NullAst(Token token) {
        super(token);
    }

    @Override
    public String toString() {
        return "null";
    }
}
