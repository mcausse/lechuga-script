package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

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
