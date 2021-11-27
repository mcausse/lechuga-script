package org.homs.lispo.eval;

import org.homs.lispo.Environment;
import org.homs.lispo.parser.ast.*;
import org.homs.lispo.tokenizer.EToken;
import org.homs.lispo.tokenizer.Token;
import org.homs.lispo.tokenizer.TokenAt;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EvaluatorTest {

    Func concat = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        StringBuilder s = new StringBuilder();
        for (var arg : args) {
            s.append(arg);
        }
        return s.toString();
    };
    Func lazyConcat = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        StringBuilder s = new StringBuilder();
        for (var arg : args) {
            s.append(ev.evalAst((Ast) arg));
        }
        return s.toString();
    };

    static Stream<Arguments> astProvider() {
        return Stream.of(
                Arguments.of(new StringAst(null, "jou"), "jou"),

                Arguments.of(new NumberAst(null, 42), 42),
                Arguments.of(new NumberAst(null, 3.14159), 3.14159),

                Arguments.of(new NullAst(null), null),

                Arguments.of(new BooleanAst(null, true), true),
                Arguments.of(new BooleanAst(null, false), false),

                Arguments.of(new SymbolAst(null, "pi"), 3.14159),
//                Arguments.of(new SymbolAst(null, "pi.class.name"), "java.lang.Double"),


                Arguments.of(new ListAst(null, Arrays.asList(
                )), Arrays.asList()),
                Arguments.of(new ListAst(null, Arrays.asList(
                        new NumberAst(null, 42),
                        new NumberAst(null, 3.14159)
                )), Arrays.asList(42, 3.14159)),


                Arguments.of(new ParenthesisAst(new Token(EToken.OPEN_LIST, "(", "test", -1, -1),
                        new SymbolAst(new Token(EToken.SYMBOL, "XXX", "test", -1, -1), "concat"),
                        Arrays.asList(
                                new StringAst(null, "alo"),
                                new StringAst(null, "jou")
                        )
                ), "alojou"),
                Arguments.of(new ParenthesisAst(new Token(EToken.OPEN_LIST, "(", "test", -1, -1),
                        new SymbolAst(new Token(EToken.SYMBOL, "XXX", "test", -1, -1), "lazy-concat"),
                        Arrays.asList(
                                new StringAst(null, "alo"),
                                new StringAst(null, "jou")
                        )
                ), "alojou")
        );
    }

    @ParameterizedTest
    @MethodSource("astProvider")
    void testEvalAst(Ast astToEvaluate, Object expectedResult) throws Throwable {
        Environment env = new Environment(null);
        env.def("pi", 3.14159);
        env.def("concat", concat);
        env.def("lazy-concat", lazyConcat);

        Evaluator evaluator = new Evaluator(env, Arrays.asList("lazy-concat"));

        Object result = evaluator.evalAst(astToEvaluate);

        assertThat(result).isEqualTo(expectedResult);
    }

}