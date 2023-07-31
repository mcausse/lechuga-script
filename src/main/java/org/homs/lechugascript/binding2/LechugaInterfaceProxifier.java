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

    /**
     * Binds the methods of an interface with the methods defined in a LechugaScript file.
     *
     * @param interfaceClass the interface class with the methods to bind.
     * @param <T>            the generic type of the interface.
     * @return a dynamic proxy
     * @throws Throwable if a runtime issue occurs when evaluating the LechugaScript code.
     */
    public static <T> T build(Class<T> interfaceClass) throws Throwable {

        final var interpreter = new Interpreter();
        final var env = new Environment(interpreter.getStdEnvironment());


        Map<Method, List<Ast>> lechugaMethodsMap = new LinkedHashMap<>();
        for (var method : interfaceClass.getMethods()) {
            if (method.isAnnotationPresent(Lechuga.class)) {
                String lechugaCode = method.getAnnotation(Lechuga.class).value();
                List<Ast> asts = interpreter.parse(lechugaCode, method.getName());
                lechugaMethodsMap.put(method, asts);
            }
        }

        var r = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (!lechugaMethodsMap.containsKey(method)) {
                            throw new RuntimeException("in method: " + method + "; annotation not found: " + Lechuga.class);
                        }

                        var env2 = new Environment(env);
                        for (int i = 0; i < method.getParameterCount(); i++) {
                            Parameter param = method.getParameters()[i];
                            if (param.isAnnotationPresent(LechugaArg.class)) {
                                String argName = param.getAnnotation(LechugaArg.class).value();
                                Object argValue = args[i];
                                env2.def(argName, argValue);
                            }
                        }

                        return interpreter.evaluate(lechugaMethodsMap.get(method), env2);
                    }

                }
        );
        return (T) r;
    }

}
