package org.homs.lechugascript;

import org.homs.lechugascript.binding.InterfaceBindingFactory;
import org.homs.lechugascript.eval.Evaluator;
import org.homs.lechugascript.eval.Func;
import org.homs.lechugascript.parser.Parser;
import org.homs.lechugascript.parser.ast.*;
import org.homs.lechugascript.tokenizer.EToken;
import org.homs.lechugascript.tokenizer.Token;
import org.homs.lechugascript.tokenizer.TokenAt;
import org.homs.lechugascript.tokenizer.Tokenizer;
import org.homs.lechugascript.util.ArithmeticFuncs;
import org.homs.lechugascript.util.ReflectUtils;
import org.homs.lechugascript.util.TextFileUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

// TODO trace: https://www2.cs.sfu.ca/CourseCentral/310/pwfong/Lisp/1/tutorial1.html
public class Interpreter {

    public static final String STD_LSP = "std.lsp";

    // TODO 0.0.3: deprecate "multi" in favor of  "let" ?
    static abstract class FuncUtils {
        public static void verifyArgumentsNumber(TokenAt tokenAt, int argumentsNumber, List<Object> args) {
            if (argumentsNumber != args.size()) {
                throw new RuntimeException("this function call requires " + argumentsNumber + " arguments, but is called with " + args.size() + "; at " + tokenAt);
            }
        }

        public static void verifyMinimumArgumentsNumber(TokenAt tokenAt, int minimumArgumentsNumber, List<Object> args) {
            if (args.size() < minimumArgumentsNumber) {
                throw new RuntimeException("this function call requires at least " + minimumArgumentsNumber + " arguments, but is called with " + args.size() + "; at " + tokenAt);
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
                throw new RuntimeException("a <" + expectedType.getName() + "> value is required, but obtained: <" + valueType + ">; at " + tokenAt);
            }
            return (T) value;
        }
    }

    // TODO experimental
    final Func funcImport = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, 1, args);
        String fileName = FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));

        var i = new Interpreter();
        var asts = i.parseFile(fileName, StandardCharsets.UTF_8);
        return i.evaluate(asts, ev.getEnvironment());
    };

    final Func funcInc = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, 1, args);

        FuncUtils.validateNotNullType(tokenAt, Ast.class, args.get(0));
        final Number vv = FuncUtils.validateNotNullType(tokenAt, Number.class, ev.evalAst((Ast) args.get(0)));

        Number vv2 = (Number) ArithmeticFuncs.funcAdd.eval(tokenAt, ev, Arrays.asList(vv, 1));

        if (args.get(0) instanceof SymbolAst) {
            String varname = ((SymbolAst) args.get(0)).value;
            ev.getEnvironment().set(varname, vv2);
        }
        return vv2;
    };

    final Func funcDec = (tokenAt, ev, args) -> {
        FuncUtils.verifyArgumentsNumber(tokenAt, 1, 1, args);

        FuncUtils.validateNotNullType(tokenAt, Ast.class, args.get(0));
        final Number vv = FuncUtils.validateNotNullType(tokenAt, Number.class, ev.evalAst((Ast) args.get(0)));

        Number vv2 = (Number) ArithmeticFuncs.funcSub.eval(tokenAt, ev, Arrays.asList(vv, 1));

        if (args.get(0) instanceof SymbolAst) {
            String varname = ((SymbolAst) args.get(0)).value;
            ev.getEnvironment().set(varname, vv2);
        }
        return vv2;
    };

    final Func funcMulti = (tokenAt, ev, args) -> {
        Evaluator ev2 = new Evaluator(ev);
        Object r = null;
        for (var arg : args) {
            r = ev2.evalAst((Ast) arg);
        }
        return r;
    };

    final Func funcLet = (tokenAt, ev, args) -> {

        MapAst bindings = Interpreter.FuncUtils.validateNotNullType(tokenAt, MapAst.class, args.get(0));

        Evaluator ev2 = new Evaluator(ev);

        /*
         * EVALUATE THE BINDINGS LIST
         */
        for (var binding : bindings.values.entrySet()) {
            Ast key = binding.getKey();
            Ast val = binding.getValue();

            SymbolAst keySymbolAst = FuncUtils.validateNotNullType(key.getTokenAt(), SymbolAst.class, key);
            Object value = ev2.evalAst(val);
            ev2.getEnvironment().def(keySymbolAst.value, value);
        }

        /*
         * EVALUATE THE BODY (LIST)
         */
        Object v = null;
        for (int i = 1; i < args.size(); i++) {
            v = ev2.evalAst((Ast) args.get(i));
        }

        return v;
    };

    final Func funcFunctionalInterface = (tokenAt, ev, args) -> {

        String functionalInterfaceClassName = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        Func func = Interpreter.FuncUtils.validateNotNullType(tokenAt, Func.class, args.get(1));
        Class<?> functionalInterfaceClass = ReflectUtils.classOf(functionalInterfaceClassName);

        return InterfaceBindingFactory.buildFunctionalInterface(functionalInterfaceClass, ev, func);
    };


    final Func funcQuote = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        return args.get(0);
    };

    final Func funcDef = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        SymbolAst symbolAst = Interpreter.FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));

        Object v = null;
        for (int i = 1; i < args.size(); i++) {
            v = ev.evalAst((Ast) args.get(i));
        }

        ev.getEnvironment().def(symbolAst.value, v);
        return v;
    };
    final Func funcSet = (TokenAt tokenAt, Evaluator ev, List<Object> args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        SymbolAst symbolAst = Interpreter.FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));

        Object v = null;
        for (int i = 1; i < args.size(); i++) {
            v = ev.evalAst((Ast) args.get(i));
        }

        ev.getEnvironment().set(symbolAst.value, v);
        return v;
    };
    final Func funcFn = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);

        ListAst argDefs = Interpreter.FuncUtils.validateNotNullType(tokenAt, ListAst.class, args.get(0));

        List<Ast> bodies = new ArrayList<>();
        for (int i = 1; i < args.size(); i++) {
            Ast body = (Ast) args.get(i);
            bodies.add(body);
        }

        return new CustomFunc(ev, argDefs, bodies);
    };
    final Func funcDefn = (tokenAt, ev, args) -> {
        SymbolAst fnNameAst = Interpreter.FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));
        ListAst argDefs = Interpreter.FuncUtils.validateNotNullType(tokenAt, ListAst.class, args.get(1));

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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, 3, args);
        Ast condition = (Ast) args.get(0);
        Ast thenPart = (Ast) args.get(1);
        Ast elsePart = null;
        if (args.size() == 3) {
            elsePart = (Ast) args.get(2);
        }

        Object conditionResult = ev.evalAst(condition);
        Interpreter.FuncUtils.validateNotNullType(tokenAt, Boolean.class, conditionResult);
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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);
        Ast condAst = (Ast) args.get(0);

        Object r = null;
        while (true) {
            Object condResult = ev.evalAst(condAst);
            Boolean cond = Interpreter.FuncUtils.validateNotNullType(tokenAt, Boolean.class, condResult);
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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);

        Iterable<?> it = Interpreter.FuncUtils.validateNotNullType(tokenAt, Iterable.class, ev.evalAst((Ast) args.get(0)));

        Object evaluatedFunc = ev.evalAst((Ast) args.get(1));
        Func func = Interpreter.FuncUtils.validateNotNullType(tokenAt, Func.class, evaluatedFunc);

        Object r = null;
        int index = 0;
        for (Object o : it) {
            Evaluator ev2 = new Evaluator(ev);
            r = func.eval(tokenAt, ev2, Arrays.asList(o, index));
            index++;
        }
        return r;
    };

    //
    // (for dog dogs
    //      (println dog))
    //
    final Func funcFor = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, null, args);

        SymbolAst elementName = FuncUtils.validateNotNullType(tokenAt, SymbolAst.class, args.get(0));
        Iterable<?> it = Interpreter.FuncUtils.validateNotNullType(tokenAt, Iterable.class, ev.evalAst((Ast) args.get(1)));

        Object r = null;
        for (Object o : it) {
            Evaluator ev2 = new Evaluator(ev);
            ev2.getEnvironment().def(elementName.value, o);

            for (int i = 2; i < args.size(); i++) {
                Ast arg = (Ast) args.get(i);
                r = ev2.evalAst(arg);
            }
        }
        return r;
    };

    final Func funcNew = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyMinimumArgumentsNumber(tokenAt, 1, args);
        String className = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));

        var argList = new ArrayList<>();
        for (int i = 1; i < args.size(); i++) {
            var evaluatedArg = args.get(i);
            argList.add(evaluatedArg);
        }
        return ReflectUtils.newInstance(className, argList.toArray());
    };

    final Func funcCallStatic = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyMinimumArgumentsNumber(tokenAt, 2, args);
        String className = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        String methodName = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(1));

        var argList = new ArrayList<>();
        for (int i = 2; i < args.size(); i++) {
            var evaluatedArg = args.get(i);
            argList.add(evaluatedArg);
        }
        return ReflectUtils.callStaticMethod(className, methodName, argList.toArray());
    };

    final Func funcFieldStatic = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 2, args);
        String className = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(0));
        String fieldName = Interpreter.FuncUtils.validateNotNullType(tokenAt, String.class, args.get(1));

        return ReflectUtils.getStaticField(className, fieldName);
    };

    final Func funcAnd = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, null, args);
        for (Object arg : args) {
            Ast valueAst = (Ast) arg;
            Boolean value = Interpreter.FuncUtils.validateNotNullType(tokenAt, Boolean.class, ev.evalAst(valueAst));
            if (!value) {
                return false;
            }
        }
        return true;
    };
    final Func funcOr = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, null, args);
        for (Object arg : args) {
            Ast valueAst = (Ast) arg;
            Boolean value = Interpreter.FuncUtils.validateNotNullType(tokenAt, Boolean.class, ev.evalAst(valueAst));
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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        CustomFunc f = Interpreter.FuncUtils.validateNotNullType(tokenAt, CustomFunc.class, args.get(0));

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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        Throwable ex = (Throwable) args.get(0);
        throw ex;
    };

    final Func funcTryCatch = (tokenAt, ev, args) -> {
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 3, args);
        Ast bodyAst = (Ast) args.get(0);
        Ast exceptionClassNameAst = (Ast) args.get(1);
        Ast catchAst = (Ast) args.get(2);

        try {
            return ev.evalAst(bodyAst);
        } catch (Throwable e) {
            String exceptionClassName = Interpreter.FuncUtils.validateNotNullType(exceptionClassNameAst.getTokenAt(), String.class, ev.evalAst(exceptionClassNameAst));
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
        Interpreter.FuncUtils.verifyArgumentsNumber(tokenAt, 1, args);
        Object o = args.get(0);
        return o == null;
    };

    /**
     * this is the root environment, with the builtin defined functions
     */
    final Environment env = new Environment(null);
    final Set<String> lazyFuncNames = new LinkedHashSet<>();

    {
        register("multi", true, funcMulti);
        register("let", true, funcLet);

        register("quote", true, funcQuote);

        register("def", true, funcDef);
        register("set", true, funcSet);
        register("fn", true, funcFn);
        register("defn", true, funcDefn);

        register("and", true, funcAnd);
        register("or", true, funcOr);

        register("if", true, funcIf);
        register("while", true, funcWhile);
        register("for-each", true, funcForEach);
        register("for", true, funcFor);

        register("new", false, funcNew);
        //register("call", false, funcCall);
        register("call-static", false, funcCallStatic);
        register("field-static", false, funcFieldStatic);

        register("new-functional-interface", false, funcFunctionalInterface);

        register("curry", false, funcCurry);

        register("throw", false, funcThrow);
        register("try-catch", true, funcTryCatch);

        register("is-null?", false, funcIsNull);

        register("import", false, funcImport);


        register("+", false, ArithmeticFuncs.funcAdd);
        register("-", false, ArithmeticFuncs.funcSub);
        register("*", false, ArithmeticFuncs.funcMul);
        register("/", false, ArithmeticFuncs.funcDiv);
        register("%", false, ArithmeticFuncs.funcMod);

        register("inc", true, funcInc);
        register("dec", true, funcDec);

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

    public Environment getEnvironment() {
        return new Environment(this.env);
    }

    public Environment getStdEnvironment() throws Throwable {
        final Environment stdEnviroenment = getEnvironment();

        final String code;
        {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream(STD_LSP);
            code = TextFileUtils.read(is, TextFileUtils.UTF8);
        }

        List<Ast> asts = parse(code, STD_LSP);
        evaluate(asts, stdEnviroenment);

        return new Environment(stdEnviroenment);
    }

    public List<Ast> parseFile(String fileName, Charset charset) throws Throwable {
        File f = new File(fileName);
        String code = TextFileUtils.read(f, charset);
        var r = parse(code, fileName);
        return r;
    }

    public List<Ast> parseFileFromClasspath(String fileName, Charset charset) throws Throwable {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream(fileName);
        var code = TextFileUtils.read(is, charset);

        var r = parse(code, fileName);
        return r;
    }

    public List<Ast> parse(String program, String sourceDesc) {
        Tokenizer tokenizer = new Tokenizer(program, sourceDesc);
        Parser parser = new Parser(tokenizer);
        List<Ast> asts = parser.parse();
        return asts;
    }

    public Object evaluate(List<Ast> asts, Environment environment) throws Throwable {
        return evaluate(asts, environment, null);
    }

    public Object evaluate(List<Ast> asts, Environment environment, BiConsumer<Environment, Ast> evaluatingAstsListener) throws Throwable {
        Evaluator evaluator = new Evaluator(environment, lazyFuncNames);
        evaluator.setEvaluatingAstsListener(evaluatingAstsListener);

        Object result = null;
        for (Ast ast : asts) {
            result = evaluator.evalAst(ast);
        }
        return result;
    }

    public Evaluator getEvaluatorInstance(Environment environment) {
        return new Evaluator(environment, lazyFuncNames);
    }
}
