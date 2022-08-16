package org.homs.lispo.eval;

import org.homs.lispo.tokenizer.TokenAt;

import java.util.List;

@FunctionalInterface
public interface Func {

    Object eval(TokenAt tokenAt, Evaluator ev, List<Object> args) throws Throwable;
}