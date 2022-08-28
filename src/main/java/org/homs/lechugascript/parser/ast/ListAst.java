package org.homs.lechugascript.parser.ast;

import org.homs.lechugascript.tokenizer.Token;

import java.util.List;
import java.util.StringJoiner;

public class ListAst extends Ast {

    public final List<Ast> values;

    public ListAst(Token token, List<Ast> values) {
        super(token);
        this.values = values;
    }

    public List<Ast> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(" ");
        for (Ast v : values) {
            j.add(v.toString());
        }
        return "[" + j + "]";
    }
}