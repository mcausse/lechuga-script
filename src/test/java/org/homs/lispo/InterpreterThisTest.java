package org.homs.lispo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterThisTest {

    static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("[ (this :add [1]) ]", Arrays.asList(1, true)),
                Arguments.of("(def m {[:age 15] [:getAge (fn [] (this :get [:age]))]})     ((m :get [:getAge]))", 15)
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void testInterpret(String expression, Object expectedResult) throws Throwable {
        Interpreter i = new Interpreter();

        var env = i.getEnvironment();
        var asts = i.parse(expression, "test");
        Object result =i.evaluate(asts, env);

        assertThat(result).isEqualTo(expectedResult);
    }
}