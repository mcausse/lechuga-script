package org.homs.lechugascript.eval;

import org.homs.lechugascript.tokenizer.TokenAt;

import java.util.List;

@FunctionalInterface
public interface Func {

    // TODO i pq no: List<Ast> args ???
    Object eval(TokenAt tokenAt, Evaluator ev, List<Object> args) throws Throwable;
}