package org.homs.lispo;

import org.homs.lispo.eval.Evaluator;
import org.homs.lispo.parser.ast.Ast;
import org.homs.lispo.parser.ast.ListAst;
import org.homs.lispo.parser.ast.SymbolAst;
import org.homs.lispo.tokenizer.TokenAt;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomFuncTest {

    final TokenAt tokenAt = new TokenAt("test", -1, -1);

    static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of(
                        new ListAst(null, Arrays.asList(new SymbolAst(null, "x"))),
                        Arrays.asList(new SymbolAst(null, "x")),
                        "(fn [x] x)",
                        Arrays.asList(42),
                        42
                ),

                Arguments.of(
                        new ListAst(null, Arrays.asList(new SymbolAst(null, "...x"))),
                        Arrays.asList(new SymbolAst(null, "x")),
                        "(fn [...x] x)",
                        Arrays.asList(1, 2, 3),
                        Arrays.asList(1, 2, 3)
                ),

                Arguments.of(
                        new ListAst(null, Arrays.asList(new SymbolAst(null, "x"), new SymbolAst(null, "y"))),
                        Arrays.asList(new SymbolAst(null, "x")),
                        "(fn [x y] x)",
                        Arrays.asList(42, 43),
                        42
                ),

                Arguments.of(
                        new ListAst(null, Arrays.asList(new SymbolAst(null, "x"), new SymbolAst(null, "y"))),
                        Arrays.asList(new SymbolAst(null, "y")),
                        "(fn [x y] y)",
                        Arrays.asList(42, 43),
                        43
                )
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void testEvalCustomFunc(ListAst customFuncArgDefs, List<Ast> customFuncBodies, String __, List<Object> invocationArgs, Object expectedResult) throws Throwable {
        CustomFunc customFunc = new CustomFunc(customFuncArgDefs, customFuncBodies);
        Evaluator ev = new Evaluator(new Environment(null), Collections.emptyList());

        Object result = customFunc.eval(tokenAt, ev, invocationArgs);

        assertThat(result).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void testToString(ListAst customFuncArgDefs, List<Ast> customFuncBodies, String expectedToString, List<Object> __1, Object __2) {
        CustomFunc customFunc = new CustomFunc(customFuncArgDefs, customFuncBodies);

        assertThat(customFunc.toString()).isEqualTo(expectedToString);
    }

}