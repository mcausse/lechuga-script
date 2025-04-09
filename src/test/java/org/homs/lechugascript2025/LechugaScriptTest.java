package org.homs.lechugascript2025;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LechugaScriptTest {

    static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of("null", null),
                Arguments.of("1", 1),
                Arguments.of("-2", -2),
                Arguments.of("+3.14159", 3.14159),
                Arguments.of("true", true),
                Arguments.of("false", false),
                Arguments.of("pi", Math.PI),

                Arguments.of("(+ pi 2)", Math.PI + 2.0),
                Arguments.of("(+(+ pi 2)(+ pi 2))", (Math.PI + 2.0) * 2.0),

                Arguments.of("[]", List.of()),
                Arguments.of("[1 pi true]", List.of(1, Math.PI, true)),

                Arguments.of("(if true 1)", 1),
                Arguments.of("(if false 1)", null),

                Arguments.of("(if true 1 :else)", 1),
                Arguments.of("(if false 1 :else)", null),

                Arguments.of("(if true 1 :else 2)", 1),
                Arguments.of("(if false 1 :else 2)", 2),

                Arguments.of("(if true 1 2 3 :else 4 5 6)", 3),
                Arguments.of("(if false 1 2 3 :else 4 5 6)", 6),

                Arguments.of("(def a 1) (def b 1 2 3) (set a (+ a b))", 4),

                Arguments.of("((fn [] 3))", 3),
                Arguments.of("((fn [] 1 2 3))", 3),
                Arguments.of("((fn [x] x) 3)", 3),
                Arguments.of("((fn [x y] (+ x y)) 2 3)", 5),
                Arguments.of("(def z 5) ((fn [x y] (+ x y z)) 2 3)", 10),

                Arguments.of("(((fn[x](fn[y](* x y)))4)6)", 24),
                Arguments.of(" ( ( ( fn [ x ] ( fn [ y ] ( *  x  y ) ) ) 4 ) 6 )", 24),
                Arguments.of("(def f (fn[x](fn[y](* x y))))  ((f 4)6)", 24),

                Arguments.of("(defn f [x] x) (f 3)", 3),
                Arguments.of("(defn f [a b c] (+ a b c)) (f 1 2 3)", 6),

                Arguments.of("(defn fact[n] (if (<= n 1) 1 :else (* n (fact (- n 1))) ))  (fact 5)", 120),

                Arguments.of("{}", Map.of()),
                Arguments.of("{1 2 3 4}", Map.of(1, 2, 3, 4)),
                Arguments.of("{1 (+ 1 1) 3 4}", Map.of(1, 2, 3, 4)),

                Arguments.of("(let{a 1 b 2 c 3}(+ a b c))", 6),
                Arguments.of("(let{a 1 b 2 c 3}a b c)", 3),
                Arguments.of("(let{a 1 b 2 c (+ a b)}a b c)", 3),

                Arguments.of("\"jou\"", "jou"),

                Arguments.of("(call (call 1 \"getClass\") \"getName\")", "java.lang.Integer"),
                Arguments.of("(new \"java.lang.Integer\" 123)", 123)
        );
    }

    @ParameterizedTest
    @MethodSource("expressionProvider")
    void name(String program, Object expectedResult) {

        // Act
        Object r = new LechugaScript().execute("test", program);

        assertThat(r).isEqualTo(expectedResult);
    }
}