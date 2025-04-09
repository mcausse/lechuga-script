package org.homs.lechugascript2025.parser.ast;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.Callable;
import org.homs.lechugascript2025.LazyCallable;
import org.homs.lechugascript2025.LechugaException;
import org.homs.lechugascript2025.lexer.Position;
import org.homs.lechugascript2025.parser.Ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunCallAst extends Ast {

    final List<Ast> asts;

    public FunCallAst(Position tokenPosition, List<Ast> asts) {
        super(tokenPosition);
        this.asts = asts;
    }

    @Override
    public Object evaluate(Environment env) {
        Callable callable = (Callable) asts.get(0).evaluate(env);
        if (callable == null) {
            throw new LechugaException("null is not a " + Callable.class.getName(), getTokenPosition());
        }

        try {
            final List<Object> args;
            if (callable instanceof LazyCallable) {
                args = new ArrayList<>(asts.subList(1, asts.size()));
            } else {
                args = asts.subList(1, asts.size()).stream().map(ast -> ast.evaluate(env)).collect(Collectors.toList());
            }

            return callable.evaluate(env, args);
        } catch (Exception e) {
            throw new LechugaException("error evaluating " + callable.getClass().getName(), getTokenPosition(), e);
        }
    }
}
