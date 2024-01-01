package org.homs.lechugascript.compiled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CompilerTest {

    Object compileAndExecute(String className, String program) throws Exception {
        final String template = "package {packageName}; " +
                "import org.homs.lechugascript.compiled.Closure;\n" +
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
                Arguments.of("NoProgram"," ;this is a comment", null),
                Arguments.of("ReturnLastStatement", "1 2 3", 3),

                Arguments.of("SumExpression", "(+ 1 2 3)", 6),
                Arguments.of("SumExpression2", "(+ (+ 1 2) (+ 0 3))", 6),
                Arguments.of("BasicDef", "(def pi 3.14159) pi", 3.14159),
                Arguments.of("BasicDef2", "(def pi 3.14159) (def pi2 (+ pi pi)) pi2", 3.14159 * 2),
                Arguments.of("BasicDef3", "(def a 2) (def b 3) (+ a b)", 5),
                Arguments.of("Def", "(+ (def con 2) con)", 4),
                Arguments.of("Def2", "(def a (def b 5)) (+ a b)", 10),
                Arguments.of("BasicFn", "((fn [a b] (+ a b)) 2 3)", 5),
                Arguments.of("FnReturnLastStatement", "((=> 1 2 3 4 5))", 5),
                Arguments.of("BasicDefn", "(def sum (fn [a b] (+ a b)))  (sum 2 3)", 5),
                Arguments.of("LambdaExpansion", "(def sum (a b => (+ a b)))   (sum 2 3)", 5),
                Arguments.of("LambdaExpansionCurry", "(def sum (a => (b => (+ a b))))   ((sum 2) 3)", 5),
                Arguments.of("DirectFnCurry", "(((fn [a] (fn [b] (+ a b)))  2) 3)", 5),
                Arguments.of("CurryDef", "(def sum (fn [a] (fn [b] (+ a b))))   ((sum 2) 3)", 5),

                Arguments.of("FunctionPass", "(def apply (fn [f a b] (f a b)))   (apply (a b => (+ a b)) 2 3)", 5)
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