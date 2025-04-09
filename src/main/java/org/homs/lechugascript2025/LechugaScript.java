package org.homs.lechugascript2025;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.lexer.Lexer;
import org.homs.lechugascript2025.parser.Ast;
import org.homs.lechugascript2025.parser.Parser;
import org.homs.lechugascript2025.runtime.ArithmeticFuncs;
import org.homs.lechugascript2025.runtime.ComparableFuncs;
import org.homs.lechugascript2025.runtime.LangFuncs;

import java.util.List;

public class LechugaScript {

    public Object execute(String scriptUrn, String script) {
        Environment env = new Environment(null);
        env.def("pi", Math.PI);

        env.def("if", LangFuncs.ifCallable);
        env.def("def", LangFuncs.defCallable);
        env.def("set", LangFuncs.setCallable);
        env.def("fn", LangFuncs.fnCallable);
        env.def("defn", LangFuncs.defnCallable);
        env.def("let", LangFuncs.letCallable);

        env.def("+", ArithmeticFuncs.funcAdd);
        env.def("-", ArithmeticFuncs.funcSub);
        env.def("*", ArithmeticFuncs.funcMul);
        env.def("/", ArithmeticFuncs.funcDiv);
        env.def("%", ArithmeticFuncs.funcMod);

        env.def("to-byte", ArithmeticFuncs.funcToByte);
        env.def("to-short", ArithmeticFuncs.funcToShort);
        env.def("to-int", ArithmeticFuncs.funcToInt);
        env.def("to-long", ArithmeticFuncs.funcToLong);
        env.def("to-float", ArithmeticFuncs.funcToFloat);
        env.def("to-double", ArithmeticFuncs.funcToDouble);

        env.def("=", ComparableFuncs.eq);
        env.def("<>", ComparableFuncs.ne);
        env.def("<", ComparableFuncs.lt);
        env.def("<=", ComparableFuncs.le);
        env.def(">", ComparableFuncs.gt);
        env.def(">=", ComparableFuncs.ge);


        return execute(new Environment(env), scriptUrn, script);
    }

    public Object execute(Environment env, String scriptUrn, String script) {
        Lexer lexer = new Lexer(scriptUrn, script);
        Parser parser = new Parser(lexer);
        List<Ast> asts = parser.parse();

        Object r = null;
        for (Ast ast : asts) {
            r = ast.evaluate(env);
        }
        return r;
    }
}
