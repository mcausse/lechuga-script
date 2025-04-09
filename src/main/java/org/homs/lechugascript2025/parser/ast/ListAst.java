package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

import java.util.List;
import java.util.stream.Collectors;

public class ListAst extends Ast {

    final List<Ast> astList;

    public ListAst(Position tokenPosition, List<Ast> astList) {
        super(tokenPosition);
        this.astList = astList;
    }

    public List<Ast> getAstList() {
        return astList;
    }

    @Override
    public Object evaluate(Environment env) {
        return astList.stream().map(ast -> ast.evaluate(env)).collect(Collectors.toList());
    }
}
