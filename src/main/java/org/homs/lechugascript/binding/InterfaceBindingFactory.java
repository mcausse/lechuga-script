package org.homs.lechugascript.binding;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.eval.Func;
import org.homs.lechugascript.parser.ast.Ast;
import org.homs.lechugascript.tokenizer.TokenAt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class InterfaceBindingFactory {

    public static <T> T build(Class<T> interfaceClass) throws Throwable {
        var r = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new InvocationHandler() {

                    final Interpreter interpreter;
                    final Environment env;

                    {
                        this.interpreter = new Interpreter();
                        this.env = new Environment(interpreter.getStdEnvironment());
                        List<Ast> asts = this.interpreter.parseFileFromClasspath(interfaceClass.getSimpleName() + ".lechuga", StandardCharsets.UTF_8);
                        this.interpreter.evaluate(asts, env);
                    }

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        final List<Object> argsList;
                        if (args == null) {
                            argsList = null;
                        } else {
                            argsList = Arrays.asList(args);
                        }

                        var r = ((Func) env.get(method.getName())).eval(
                                new TokenAt(interfaceClass.getName(), 0, 0),
                                interpreter.getEvaluatorInstance(env),
                                argsList);

                        return r;
                    }
                });
        return (T) r;
    }

}
