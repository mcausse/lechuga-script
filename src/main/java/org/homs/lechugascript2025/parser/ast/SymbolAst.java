package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.LechugaException;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

public class SymbolAst extends Ast {

    final String symbol;

    public SymbolAst(Position tokenPosition, String tokenValue) {
        super(tokenPosition);
        this.symbol = tokenValue;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public Object evaluate(Environment env) {
        try {
            return env.get(this.symbol);
        } catch (Exception e) {
            throw new LechugaException("variable not defined: '" + symbol + "'", getTokenPosition(), e);
        }
    }
}
