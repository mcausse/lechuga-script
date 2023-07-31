package org.homs.lechugascript.binding2;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript.Interpreter;
import org.homs.lechugascript.parser.ast.Ast;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LechugaInterfaceProxifier {

    public static <T> T build(Class<T> interfaceClass) throws Throwable {

        final var interpreter = new Interpreter();
        final var env = new Environment(interpreter.getStdEnvironment());

        /*
         * parse all @Lechuga-annotated methods of the interface
         */
        Map<Method, List<Ast>> lechugaMethodsMap = new LinkedHashMap<>();
        for (var method : interfaceClass.getMethods()) {
            if (method.isAnnotationPresent(Lechuga.class)) {
                String lechugaCode = method.getAnnotation(Lechuga.class).value();
                List<Ast> asts = interpreter.parse(lechugaCode, method.getName());
                lechugaMethodsMap.put(method, asts);
            }
        }

        var r = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new LechugaInvocationHandler(interpreter, lechugaMethodsMap, env)
        );
        return (T) r;
    }

    static class LechugaInvocationHandler implements InvocationHandler {

        final Interpreter interpreter;
        final Map<Method, List<Ast>> lechugaMethodsMap;
        final Environment baseEnvironment;

        public LechugaInvocationHandler(Interpreter interpreter, Map<Method, List<Ast>> lechugaMethodsMap, Environment baseEnvironment) {
            this.interpreter = interpreter;
            this.lechugaMethodsMap = lechugaMethodsMap;
            this.baseEnvironment = baseEnvironment;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!lechugaMethodsMap.containsKey(method)) {
                throw new RuntimeException("in method: " + method + "; annotation not found: " + Lechuga.class);
            }

            var invocationEnv = new Environment(baseEnvironment);
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter param = method.getParameters()[i];
                if (param.isAnnotationPresent(LechugaArg.class)) {
                    String argName = param.getAnnotation(LechugaArg.class).value();
                    Object argValue = args[i];
                    invocationEnv.def(argName, argValue);
                }
            }

            return interpreter.evaluate(lechugaMethodsMap.get(method), invocationEnv);
        }
    }

}
