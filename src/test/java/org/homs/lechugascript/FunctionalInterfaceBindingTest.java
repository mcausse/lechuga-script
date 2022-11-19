package org.homs.lechugascript;

import org.homs.lechugascript.binding.InterfaceBindingFactory;
import org.homs.lechugascript.eval.Evaluator;
import org.homs.lechugascript.eval.Func;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionalInterfaceBindingTest {

    @Test
    void test_Supplier() throws Throwable {
        String program = "(=> (* 2 pi))";

        var i = new Interpreter();
        var env = i.getEnvironment();
        env.def("pi", Math.PI);
        var asts = i.parse(program, "test");
        Func lechugaFunc = (Func) i.evaluate(asts, env);
        Evaluator ev = i.getEvaluatorInstance(env);

        Supplier<Double> f = (Supplier<Double>) InterfaceBindingFactory.buildFunctionalInterface(Supplier.class, ev, lechugaFunc);

        assertThat(f.get()).isEqualTo(Math.PI * 2.0);
    }

    @Test
    void test_Function() throws Throwable {
        String program = "(s => (to-long s))";

        var i = new Interpreter();
        var env = i.getEnvironment();
        var asts = i.parse(program, "test");
        Func lechugaFunc = (Func) i.evaluate(asts, env);
        Evaluator ev = i.getEvaluatorInstance(env);

        Function<String, Long> f = (Function<String, Long>) InterfaceBindingFactory.buildFunctionalInterface(Function.class, ev, lechugaFunc);

        assertThat(f.apply("123")).isEqualTo(123L);
    }

    @Test
    void test_BiFunction() throws Throwable {
        String program = "(a b => (to-int (* a b)))";

        var i = new Interpreter();
        var env = i.getEnvironment();
        var asts = i.parse(program, "test");
        Func lechugaFunc = (Func) i.evaluate(asts, env);
        Evaluator ev = i.getEvaluatorInstance(env);

        BiFunction<Integer, Integer, Integer> f = (BiFunction<Integer, Integer, Integer>) InterfaceBindingFactory.buildFunctionalInterface(BiFunction.class, ev, lechugaFunc);

        assertThat(f.apply(3, 4)).isEqualTo(12);
    }

    @Test
    void test_BiFunction_Real() throws Throwable {
        String program = "(def bi-function (new-functional-interface :java.util.function.BiFunction (a b => (* a b pi))))" +
                "(bi-function :apply 3 4)";

        var i = new Interpreter();
        var env = i.getEnvironment();
        env.def("pi", Math.PI);
        var asts = i.parse(program, "test");

        double r = (Double) i.evaluate(asts, env);

        assertThat(r).isEqualTo(Math.PI * 3.0 * 4.0);
    }
}
