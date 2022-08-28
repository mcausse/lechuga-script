package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;
import org.homs.lechugascript.tokenizer.TokenAt;

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