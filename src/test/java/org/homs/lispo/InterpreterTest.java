package org.homs.lispo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InterpreterTest {

    final static Map<Integer,String> ordinalsMap = new LinkedHashMap<>();
    static {
        ordinalsMap.put(1, "one");
        ordinalsMap.put(2, "two");
        ordinalsMap.put(3, "three");
    }

    static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of("42", 42),
                Arguments.of("3.14159", 3.14159),
                Arguments.of("null", null),
                Arguments.of("true", true),
                Arguments.of("false", false),
                Arguments.of("\"jou\"", "jou"),
                Arguments.of(":jou", "jou"),
                Arguments.of("[42 3.14159 \"jou\" null false]", Arrays.asList(42,3.14159,"jou",null,false)),
                Arguments.of("{[1 :one][2 :two][3 :three]}", ordinalsMap)
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void testInterpret(String expression, Object expectedResult) {
        Interpreter i = new Interpreter();

        Object result = i.interpret(expression, "test");

        assertThat(result).isEqualTo(expectedResult);
    }
}