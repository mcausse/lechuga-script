package org.homs.lispo;

import org.homs.lispo.eval.Evaluator;
import org.homs.lispo.eval.Func;
import org.homs.lispo.parser.Parser;
import org.homs.lispo.parser.ast.Ast;
import org.homs.lispo.parser.ast.ListAst;
import org.homs.lispo.parser.ast.ParenthesisAst;
import org.homs.lispo.parser.ast.SymbolAst;
import org.homs.lispo.tokenizer.EToken;
import org.homs.lispo.tokenizer.Token;
import org.homs.lispo.tokenizer.TokenAt;
import org.homs.lispo.tokenizer.Tokenizer;
import org.homs.lispo.util.ArithmeticFuncs;
import org.homs.lispo.util.ReflectUtils;
import org.homs.lispo.util.TextFileUtils;

import java.io.File;
import java.util.*;

public class Interpreter {

    static abstract class FuncUtils {
        public static void verifyArgumentsNumber(TokenAt tokenAt, int argumentsNumber, List<Object> args) {
            if (argumentsNumber != args.size()) {
                throw new RuntimeException("this function call requires " + argumentsNumber + " arguments, but is called with " + args.size() + "; at " + tokenAt);
            }
        }

        public static void verifyArgumentsNumber(TokenAt tokenAt, Integer minArgumentsNumber, Integer maxArgumentsNumber, List<Object> args) {
            if ((minArgumentsNumber != null && args.size() < minArgumentsNumber) || (maxArgumentsNumber != null && maxArgumentsNumber < args.size())) {
                throw new RuntimeException("this function call requires " + minArgumentsNumber + ".." + maxArgumentsNumber + " arguments, but is called with " + args.size() + "; at " + tokenAt);
            }
        }

        public static <T> T validateNotNullType(TokenAt tokenAt, Class<T> expectedType, Object value) {

            if (value == null || !expectedType.isAssignableFrom(value.getClass())) {
                String valueType;
                if (value == null) {
                    valueType = "null";
                } else {
                    valueType = value.getClass().getName();
                }
                throw new RuntimeException("a <" + Boolean.class.getName() + "> value is required, but obtained: <" + valueType + ">; at " + tokenAt);
            }
            return (T) value;
        }
    }

    final Func funcMulti = (tokenAt, ev, args) -> args.isEmpty() ? null : args.get(args.size() - 1);

    final Func funcDef = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        SymbolAst symbolAst = FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));

        Object v = null;
        for (int i = 1; i < args.size(); i++) {
            v = ev.evalAst((Ast) args.get(i));
        }

        ev.getEnvironment().def(symbolAst.value, v);
        return v;
    };
    final Func funcSet = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        SymbolAst symbolAst = FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));

        Object v = null;
        for (int i = 1; i < args.size(); i++) {
            v = ev.evalAst((Ast) args.get(i));
        }

        ev.getEnvironment().set(symbolAst.value, v);
        return v;
    };
    final Func funcFn = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);

        ListAst argDefs = FuncUtils.validateNotNullType(tokenAt, ListAst.class, args.get(0));

        List<Ast> bodies = new ArrayList<>();
        for (int i = 1; i < args.size(); i++) {
            Ast body = (Ast) args.get(i);
            bodies.add(body);
        }

        return new CustomFunc(ev, argDefs, bodies);
    };
    final Func funcDefn = (tokenAt, ev, args) -> {
        SymbolAst fnNameAst = FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));
        ListAst argDefs = FuncUtils.validateNotNullType(tokenAt, ListAst.class, args.get(1));

        List<Ast> bodies = new ArrayList<>();
        for (int i = 2; i < args.size(); i++) {
            Ast body = (Ast) args.get(i);
            bodies.add(body);
        }
        Object r = new CustomFunc(ev, argDefs, bodies);

        ev.getEnvironment().def(fnNameAst.value, r);
        return r;
    };
    final Func funcIf = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, 3, args);
        Ast condition = (Ast) args.get(0);
        Ast thenPart = (Ast) args.get(1);
        Ast elsePart = null;
        if (args.size() == 3) {
            elsePart = (Ast) args.get(2);
        }

        Object conditionResult = ev.evalAst(condition);
        FuncUtils.validateNotNullType(tokenAt, Boolean.class, conditionResult);
        if ((Boolean) conditionResult) {
            Evaluator ev2 = new Evaluator(ev);
            return ev2.evalAst(thenPart);
        } else {
            if (elsePart == null) {
                return null;
            } else {
                Evaluator ev2 = new Evaluator(ev);
                return ev2.evalAst(elsePart);
            }
        }
    };
    final Func funcWhile = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        Ast condAst = (Ast) args.get(0);

        Object r = null;
        while (true) {
            Object condResult = ev.evalAst(condAst);
            Boolean cond = FuncUtils.validateNotNullType(tokenAt, Boolean.class, condResult);
            if (!cond) {
                break;
            }

            Evaluator ev2 = new Evaluator(ev);
            for (int i = 1; i < args.size(); i++) {
                Ast arg = (Ast) args.get(i);
                r = ev2.evalAst(arg);
            }
        }
        return r;
    };

    final Func funcForEach = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);

        Object evaluatedFunc = ev.evalAst((Ast) args.get(0));
        Func func = FuncUtils.validateNotNullType(tokenAt, Func.class, evaluatedFunc);

        Iterable<Object> it = FuncUtils.validateNotNullType(tokenAt, Iterable.class, ev.evalAst((Ast) args.get(1)));

        Object r = null;
        int index = 0;
        for (Object o : it) {
            Evaluator ev2 = new Evaluator(ev);
            r = func.eval(tokenAt, ev2, Arrays.asList(o, index));
            index++;
        }
        return r;
    };

    final Func funcNew = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, args);
        String className = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        List<?> argList = FuncUtils.validateNotNullType(tokenAt, List.class, args.get(1));

        return ReflectUtils.newInstance(className, argList.toArray());
    };

    final Func funcCallStatic = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 3, args);
        String className = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        String methodName = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(1));
        List<?> argList = FuncUtils.validateNotNullType(tokenAt, List.class, args.get(2));

        return ReflectUtils.callStaticMethod(className, methodName, argList.toArray());
    };

    final Func funcFieldStatic = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 2, args);
        String className = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        String fieldName = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(1));

        return ReflectUtils.getStaticField(className, fieldName);
    };

    final Func funcAnd = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, null, args);
        for (Object arg : args) {
            Ast valueAst = (Ast) arg;
            Boolean value = FuncUtils.validateNotNullType(tokenAt, Boolean.class, ev.evalAst(valueAst));
            if (!value) {
                return false;
            }
        }
        return true;
    };
    final Func funcOr = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, null, args);
        for (Object arg : args) {
            Ast valueAst = (Ast) arg;
            Boolean value = FuncUtils.validateNotNullType(tokenAt, Boolean.class, ev.evalAst(valueAst));
            if (value) {
                return true;
            }
        }
        return false;
    };

    /*
         (fn [x y z]
             (* x y z))

         (fn [x y]
             (fn [z]
                 (* x y z)))
    */
    final Func funcCurry = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        CustomFunc f = FuncUtils.validateNotNullType(tokenAt, CustomFunc.class, args.get(0));

        final List<Ast> argDefsHead;
        final List<Ast> argDefsTail;
        {
            List<Ast> argDefs = f.argDefs.values;
            argDefsTail = argDefs.subList(0, argDefs.size() - 1);
            argDefsHead = Arrays.asList(argDefs.get(argDefs.size() - 1));
        }

        Token fnToken = new Token(EToken.SYMBOL, "fn", tokenAt);
        List<Ast> fn2Args = new ArrayList<>();
        fn2Args.add(new ListAst(fnToken, argDefsHead));
        fn2Args.addAll(f.bodies);

        ParenthesisAst fn2 = new ParenthesisAst(fnToken, new SymbolAst(fnToken, "fn"), fn2Args);

        return new CustomFunc(ev, new ListAst(fnToken, argDefsTail), Arrays.asList(fn2));
    };

    final Func funcThrow = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        Throwable ex = (Throwable) args.get(0);
        throw ex;
    };

    final Func funcTryCatch = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 3, args);
        Ast bodyAst = (Ast) args.get(0);
        Ast exceptionClassNameAst = (Ast) args.get(1);
        Ast catchAst = (Ast) args.get(2);

        try {
            return ev.evalAst(bodyAst);
        } catch (Throwable e) {
            String exceptionClassName = FuncUtils.validateNotNullType(exceptionClassNameAst.getTokenAt(), String.class, ev.evalAst(exceptionClassNameAst));
            Class<?> exceptionClass;
            try {
                exceptionClass = Class.forName(exceptionClassName);
            } catch (ClassNotFoundException classNotFoundException) {
                throw new RuntimeException(classNotFoundException);
            }
            if (exceptionClass.isAssignableFrom(e.getClass())) {
                Func catchFunc = (Func) ev.evalAst(catchAst);
                return catchFunc.eval(tokenAt, ev, Arrays.asList(e, tokenAt));
            } else {
                throw e;
            }
        }
    };

    final Func funcIsNull = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        Object o = args.get(0);
        return o == null;
    };


    final Environment env = new Environment(null);
    final Set<String> lazyFuncNames = new LinkedHashSet<>();

    {
        register("multi", false, funcMulti);

        register("def", true, funcDef);
        register("set", true, funcSet);
        register("fn", true, funcFn);
        register("defn", true, funcDefn);

        register("and", true, funcAnd);
        register("or", true, funcOr);

        register("if", true, funcIf);
        register("while", true, funcWhile);
        register("for-each", true, funcForEach);

        register("new", false, funcNew);
        //register("call", false, funcCall);
        register("call-static", false, funcCallStatic);
        register("field-static", false, funcFieldStatic);

        register("curry", false, funcCurry);

        register("throw", false, funcThrow);
        register("try-catch", true, funcTryCatch);

        register("is-null?", false, funcIsNull);


        register("+", false, ArithmeticFuncs.funcAdd);
        register("-", false, ArithmeticFuncs.funcSub);
        register("*", false, ArithmeticFuncs.funcMul);
        register("/", false, ArithmeticFuncs.funcDiv);
        register("%", false, ArithmeticFuncs.funcMod);

        register("to-byte", false, ArithmeticFuncs.funcToByte);
        register("to-short", false, ArithmeticFuncs.funcToShort);
        register("to-int", false, ArithmeticFuncs.funcToInt);
        register("to-long", false, ArithmeticFuncs.funcToLong);
        register("to-float", false, ArithmeticFuncs.funcToFloat);
        register("to-double", false, ArithmeticFuncs.funcToDouble);
    }

    public void register(String name, Object value) {
        register(name, false, value);
    }

    public void register(String name, boolean isLazyFunc, Object value) {
        env.def(name, value);
        if (isLazyFunc) {
            lazyFuncNames.add(name);
        }
    }

    public Object interpret(String program, String sourceDesc) throws Throwable {
        Tokenizer tokenizer = new Tokenizer(program, sourceDesc);
        Parser parser = new Parser(tokenizer);
        Evaluator evaluator = new Evaluator(new Environment(env), lazyFuncNames);

        Object result = null;
        List<Ast> asts = parser.parse();
        for (Ast ast : asts) {
            result = evaluator.evalAst(ast);
        }
        return result;
    }

    public Object evalClassPathFile(String fileName) throws Throwable {
        String f = getClass().getClassLoader().getResource(fileName).getFile();
        return evalFile(f);
    }

    public Object evalFile(String fileName) throws Throwable {
        File f = new File(fileName);
        String code = TextFileUtils.read(f, TextFileUtils.UTF8);
        Object r = interpret(code, fileName);
        return r;
    }
}
