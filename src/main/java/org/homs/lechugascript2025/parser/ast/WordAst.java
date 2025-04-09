package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

public class WordAst extends Ast {

    final String word;

    public WordAst(Position tokenPosition, String tokenValue) {
        super(tokenPosition);
        this.word = tokenValue;
    }

    public String getWord() {
        return word;
    }

    @Override
    public Object evaluate(Environment env) {
//            throw new RuntimeException // TODO
        return this; // TODO ?
    }
}
