package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;

public class BooleanAst extends Ast {

    public final boolean value;

    public BooleanAst(Token token, boolean value) {
        super(token);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}