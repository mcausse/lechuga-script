package org.homs.lechugascript;

import org.homs.lechugascript.eval.Evaluator;
import org.homs.lechugascript.tokenizer.TokenAt;

import java.util.Collections;
import java.util.function.Function;

// TODO i que passa amb això?
public class FunctionInterface implements Function<Object, Object> {

    final TokenAt tokenAt;
    final Evaluator evaluator;
    final CustomFunc func;

    public FunctionInterface(TokenAt tokenAt, Evaluator evaluator, CustomFunc func) {
        this.tokenAt = tokenAt;
        this.evaluator = evaluator;
        this.func = func;
    }

    @Override
    public Object apply(Object arg) {
        try {
            return func.eval(tokenAt, evaluator, Collections.singletonList(arg));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
