package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

import java.util.Map;
import java.util.StringJoiner;

public class MapAst extends Ast {

    public final Map<Ast, Ast> values;

    public MapAst(Token token, Map<Ast, Ast> values) {
        super(token);
        this.values = values;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(" ");
        for (Map.Entry<Ast, Ast> v : values.entrySet()) {
            j.add("[" + v.getKey() + " " + v.getValue() + "]");
        }
        return "{" + j + "}";
    }
}