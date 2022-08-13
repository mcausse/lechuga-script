package org.homs.lispo.parser.ast;

import org.homs.lispo.tokenizer.Token;

import java.util.Collection;

public class InterpolationStringAst extends Ast {

    public final String value;
    public final Collection<Ast> templateParts;

    public InterpolationStringAst(Token token, String value, Collection<Ast> templateParts) {
        super(token);
        this.value = value;
        this.templateParts = templateParts;
    }

    @Override
    public String toString() {
        return "\"\"\"" + value + "\"\"\"";
    }
}
