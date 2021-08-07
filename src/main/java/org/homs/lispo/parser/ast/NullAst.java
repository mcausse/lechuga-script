package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

public class NullAst extends Ast {

    public NullAst(Token token) {
        super(token);
    }

    @Override
    public String toString() {
        return "null";
    }
}
