package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

public class SymbolAst extends Ast {

    public String value;

    public SymbolAst(Token token, String value) {
        super(token);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}