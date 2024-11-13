package org.homs.lechugascript.compiled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CompilerTest {

    Object compileAndExecute(String className, String program) throws Exception {
        final String template = "package {packageName};\n" +
                "public class {className} {\n" +
                "    public Object test() {\n" +
                "       {visitor}\n" +
                "       return {returnVarname};\n" +
                "    }\n" +
                "}";
        final Compiler compiler = new Compiler(template, "./compilud");
        Class<?> compiledClass = compiler.compile("org.lechuga", className, program, className);

        // invoke
        Object instance = compiledClass.getDeclaredConstructor().newInstance();
        Object result = instance.getClass().getDeclaredMethod("test").invoke(instance);
        System.out.println("=> " + result);
        return result;
    }

    static Stream<Arguments> basicExpressionsProvider() {
        return Stream.of(
                Arguments.of("NoProgram", " ;this is a comment", null),
                Arguments.of("ReturnLastStatement", "1 2 3", 3),

                Arguments.of("SumExpression", "(+ 1 2 3)", 6),
                Arguments.of("SumExpression2", "(+ (+ 1 2) (+ 0 3))", 6),
                Arguments.of("BasicDef", "(def pi 3.14159) pi", 3.14159),
                Arguments.of("BasicDef2", "(def pi 3.14159) (def pi2 (+ pi pi)) pi2", 3.14159 * 2),
                Arguments.of("BasicDef3", "(def a 2) (def b 3) (+ a b)", 5),
                Arguments.of("Def", "(+ (def con 2) con)", 4),
                Arguments.of("Def2", "(def a (def b 5)) (+ a b)", 10),

                Arguments.of("MultibodyDef", "(def a (+ 1 2) (+ 2 3) (+ 3 4))", 7),
                Arguments.of("MultibodyDef2", "(def a (+ 1 2) (+ 2 3) (+ 3 4)) a", 7),

                Arguments.of("BasicFn", "((fn [a b] (+ a b)) 2 3)", 5),
                Arguments.of("FnReturnLastStatement", "((=> 1 2 3 4 5))", 5),
                Arguments.of("BasicDefn", "(def sum (fn [a b] (+ a b)))  (sum 2 3)", 5),
                Arguments.of("LambdaExpansion", "(def sum (a b => (+ a b)))   (sum 2 3)", 5),
                Arguments.of("LambdaExpansionCurry", "(def sum (a => (b => (+ a b))))   ((sum 2) 3)", 5),
                Arguments.of("DirectFnCurry", "(((fn [a] (fn [b] (+ a b)))  2) 3)", 5),
                Arguments.of("CurryDef", "(def sum (fn [a] (fn [b] (+ a b))))   ((sum 2) 3)", 5),

                Arguments.of("FunctionPass", "(def apply (fn [f a b] (f a b)))   (apply (a b => (+ a b)) 2 3)", 5),

//                Arguments.of("equals", "(= 23 23 23)", true),
                Arguments.of("SimpleLogic0", "(and true true (or false false (= 23 23)))", true),
                Arguments.of("And1", "(and (= 1 1))", true),
                Arguments.of("And2", "(and (= 1 1) (= 2 2))", true),
                Arguments.of("And3", "(and (= 1 1) (= 2 2) (= 3 3))", true),
                Arguments.of("And4", "(and (= 1 1) (= 2 2222) (= 3 3))", false),

                Arguments.of("Or1", "(or true)", true),
                Arguments.of("Or2", "(or true true)", true),
                Arguments.of("Or3", "(or true true true)", true),
                Arguments.of("Or4", "(or false)", false),
                Arguments.of("Or5", "(or (= 1 1) (<> 1 1))", true),
                Arguments.of("Or6", "(or true false true)", true),


                Arguments.of("If0", "(if (= 2 2) 3)", 3),
                Arguments.of("If1", "(if (<> 2 2) 3)", null),
                Arguments.of("If2", "(if (= 2 2) 3 4)", 3),
                Arguments.of("If3", "(if (<> 2 2) 3 4)", 4),
                Arguments.of("If4", "(def jou (if (= 2 2) 3 4)) jou", 3),

                Arguments.of("While1", "(def i 0)(while (< i 5) (set i (+ i 1)))", 5),

                Arguments.of("Let1", "(let {} 2)", 2),
                Arguments.of("Let2", "(let {[a 3]} 1 2 a)", 3),
                Arguments.of("Let3", "(let {[one 1][two 2][three 3]} (+ one two three))", 6),
                Arguments.of("Let4", "(let {[a 3][b (+ a 1)]} b)", 4),

//                Arguments.of("Fact",
//                        "(def fact (x => " +
//                                "        (if (= x 1) " +
//                                "           1 " +
//                                "           (* x (fact (- x 1))) " +
//                                "        )" +
//                                "))"
//                        , 5)

                Arguments.of("Fact1",
                        "(def fact (n => " +
                                "(def r 1)" +
                                "(def i 2) " +
                                "(while (<= i n) " +
                                "   (set r (* r i))" +
                                "   (set i (+ i 1))" +
                                ") " +
                                "r " +
                                "))" +

                                "(fact 5)"
                        , 120),


                Arguments.of("NoProblem", "(def i 2)(set i (+ i 1))", 3),

                Arguments.of("Fact2",
                        "(def fact (n => " +
                                "(let {[r 1] [i 2]}" +
                                "   (while (<= i n) " +
                                "       (set r (* r i))" +
                                "       (set i (+ i 1))" +
                                "   ) " +
                                "   r)" +
                                "))" +

                                "(fact 5)"
                        , 120),

                Arguments.of("Compose",
                        "(def mul2 (fn [a] (* a 2)))" +
                                "(def add3 (fn [a] (+ a 3)))" +
                                "(mul2 (add3 2))"
                        , 10),

                Arguments.of("Direct1", "(.java.lang.Math.min 1 3)", 1),
                Arguments.of("Direct1", "(.java.lang.Math.max 1 3)", 3)
        );
    }

    @ParameterizedTest
    @MethodSource("basicExpressionsProvider")
    void basicExpressions(String targetClassName, String code, Object expectedResult) throws Exception {
        assertThat(
                compileAndExecute(targetClassName, code)
        ).isEqualTo(expectedResult);
    }

    @Test
    void atomTypes() throws Exception {
        assertThat(
                compileAndExecute("AtomTypes", "[1 2.0 true false null :jou []]")
        ).hasToString("[1, 2.0, true, false, null, jou, []]");
    }
}