package org.homs.lispo.eval;

import org.homs.lispo.parser.ast.Ast;
import org.homs.lispo.tokenizer.TokenAt;

import java.util.List;

@FunctionalInterface
public interface Func {

    // TODO i pq no: List<Ast> args ???
    Object eval(TokenAt tokenAt, Evaluator ev, List<Object> args) throws Throwable;
}