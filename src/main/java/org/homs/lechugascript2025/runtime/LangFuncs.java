package org.homs.lechugascript2025.runtime;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.Callable;
import org.homs.lechugascript2025.CustomCallable;
import org.homs.lechugascript2025.LazyCallable;
import org.homs.lechugascript2025.LechugaException;
import org.homs.lechugascript2025.parser.Ast;
import org.homs.lechugascript2025.parser.ast.ListAst;
import org.homs.lechugascript2025.parser.ast.MapAst;
import org.homs.lechugascript2025.parser.ast.SymbolAst;
import org.homs.lechugascript2025.parser.ast.WordAst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LangFuncs {

    public static LazyCallable ifCallable = (env, astArgs) -> {

        if (astArgs.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }
        boolean cond = ((Boolean) ((Ast) astArgs.get(0)).evaluate(env));

        Object r = null;
        int i;
        for (i = 1; i < astArgs.size(); i++) {
            Ast ast = (Ast) astArgs.get(i);
            if ((ast instanceof WordAst) && ((WordAst) ast).getWord().equals("else")) {
                break;
            }
            if (cond) {
                r = ast.evaluate(env);
            }
        }

        // evalua els bodies de l'else
        for (int j = i + 1; j < astArgs.size(); j++) {
            Ast ast = (Ast) astArgs.get(j);
            if (!cond) {
                r = ast.evaluate(env);
            }
        }

        return r;
    };
    public static LazyCallable defCallable = (env, astArgs) -> {

        if (astArgs.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }

        String symbol = ((SymbolAst) astArgs.get(0)).getSymbol();

        Object r = null;
        for (int i = 1; i < astArgs.size(); i++) {
            Ast arg = (Ast) astArgs.get(i);
            r = arg.evaluate(env);
        }
        env.def(symbol, r);

        return r;
    };
    public static LazyCallable setCallable = (env, astArgs) -> {

        if (astArgs.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }

        String symbol = ((SymbolAst) astArgs.get(0)).getSymbol();

        Object r = null;
        for (int i = 1; i < astArgs.size(); i++) {
            Ast arg = (Ast) astArgs.get(i);
            r = arg.evaluate(env);
        }
        env.set(symbol, r);

        return r;
    };

    public static LazyCallable fnCallable = (env, astArgs) -> {

        if (astArgs.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }

        List<Ast> symbolAsts = ((ListAst) astArgs.get(0)).getAstList();
        List<String> argNames = new ArrayList<>();
        for (Ast symbolAst : symbolAsts) {
            if (!(symbolAst instanceof SymbolAst)) {
                throw new LechugaException("expected all arguments of type " + SymbolAst.class.getName() + ", but: " + symbolAst.getClass().getName(),
                        symbolAst.getTokenPosition());
            }
            argNames.add(((SymbolAst) symbolAst).getSymbol());
        }

        List<Ast> bodies = astArgs.subList(1, astArgs.size()).stream().map(a -> (Ast) a).collect(Collectors.toList());

        return new CustomCallable(argNames, bodies, env);
    };
    public static LazyCallable defnCallable = (env, astArgs) -> {

        if (astArgs.size() < 3) {
            throw new RuntimeException("required at least 3 args");
        }

        String fnName = (((SymbolAst) astArgs.get(0))).getSymbol();
        List<Ast> symbolAsts = ((ListAst) astArgs.get(1)).getAstList();
        List<String> argNames = new ArrayList<>();
        for (Ast symbolAst : symbolAsts) {
            if (!(symbolAst instanceof SymbolAst)) {
                throw new LechugaException("expected all arguments of type " + SymbolAst.class.getName() + ", but: " + symbolAst.getClass().getName(),
                        symbolAst.getTokenPosition());
            }
            argNames.add(((SymbolAst) symbolAst).getSymbol());
        }

        List<Ast> bodies = astArgs.subList(1, astArgs.size()).stream().map(a -> (Ast) a).collect(Collectors.toList());

        var r = new CustomCallable(fnName, argNames, bodies, env);
        env.def(fnName, r);
        return r;
    };

    public static LazyCallable letCallable = (env, astArgs) -> {

        if (astArgs.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }

        Environment env2 = new Environment(env);
        MapAst mapast = ((MapAst) astArgs.get(0));
        for (Map.Entry<Ast, Ast> entry : mapast.getAstsMap().entrySet()) {
            String key = ((SymbolAst) entry.getKey()).getSymbol();
            Object value = entry.getValue().evaluate(env2);
            env2.def(key, value);
        }

        Object r = null;
        for (int i = 1; i < astArgs.size(); i++) {
            r = ((Ast) astArgs.get(i)).evaluate(env2);
        }

        return r;
    };

    public static Callable callJavaCallable = (env, args) -> {

        if (args.size() < 2) {
            throw new RuntimeException("required at least 2 args");
        }
        Object target = args.get(0);
        String methodName = (String) args.get(1);
        List<Object> callArgs = args.subList(2, args.size());

        return ReflectUtils.callMethod(target, methodName, callArgs);
    };

    public static Callable newJavaCallable = (env, args) -> {

        if (args.isEmpty()) {
            throw new RuntimeException("required at least 1 arg");
        }
        String className = (String) args.get(0);
        List<Object> callArgs = args.subList(1, args.size());

        return ReflectUtils.newInstance(className, callArgs.toArray());
    };

}
