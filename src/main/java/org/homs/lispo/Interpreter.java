package org.homs.lispo;

import org.homs.lispo.eval.Evaluator;
import org.homs.lispo.parser.Parser;
import org.homs.lispo.parser.ast.Ast;
import org.homs.lispo.tokenizer.Tokenizer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Interpreter {

    final Environment env = new Environment(null);
    final Set<String> lazyFuncNames = new LinkedHashSet<>();

    public void register(String name, Object value) {
        register(name, false, value);
    }

    public void register(String name, boolean isLazyFunc, Object value) {
        env.def(name, value);
        if (isLazyFunc) {
            lazyFuncNames.add(name);
        }
    }


    public Object interpret(String program, String sourceDesc) {
        Tokenizer tokenizer = new Tokenizer(program, sourceDesc);
        Parser parser = new Parser(tokenizer);

        List<Ast> asts = parser.parse();


        Evaluator evaluator = new Evaluator(new Environment(env), lazyFuncNames);

        Object result = null;
        for (Ast ast : asts) {
            result = evaluator.evalAst(ast);
        }
        return result;
    }
}
