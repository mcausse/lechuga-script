package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;

public class StringAst extends Ast {

    public final String value;

    public StringAst(Token token, String value) {
        super(token);
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
