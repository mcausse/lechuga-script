package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;
import org.homs.lispo.tokenizer.TokenAt;

public abstract class Ast {

    final Token token;

    public Ast(Token token) {
        super();
        this.token = token;
    }

    public TokenAt getTokenAt() {
        return token.tokenAt;
    }

}