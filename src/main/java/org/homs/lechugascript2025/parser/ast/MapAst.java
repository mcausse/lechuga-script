package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapAst extends Ast {

    final Map<Ast, Ast> astsMap;

    public MapAst(Position tokenPosition, Map<Ast, Ast> astsMap) {
        super(tokenPosition);
        this.astsMap = astsMap;
    }

    public Map<Ast, Ast> getAstsMap() {
        return astsMap;
    }

    @Override
    public Object evaluate(Environment env) {
        Map<Object, Object> r = new LinkedHashMap<>();
        astsMap.forEach((key, value) -> r.put(key.evaluate(env), value.evaluate(env)));
        return r;
    }
}
