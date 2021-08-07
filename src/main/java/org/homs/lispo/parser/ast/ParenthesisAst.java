package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

import java.util.List;
import java.util.StringJoiner;

public class ParenthesisAst extends Ast {

    public final Ast operator;
    public final List<Ast> arguments;

    public ParenthesisAst(Token token, Ast operator, List<Ast> arguments) {
        super(token);
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(" ");
        j.add(operator.toString());
        for (Ast v : arguments) {
            j.add(v.toString());
        }
        return "(" + j + ")";
    }
}