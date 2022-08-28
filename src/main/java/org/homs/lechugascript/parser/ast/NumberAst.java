package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;

public class NumberAst extends Ast {

    public final Number value;

    public NumberAst(Token token, Number value) {
        super(token);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}