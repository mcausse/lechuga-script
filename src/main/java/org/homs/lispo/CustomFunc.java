package org.homs.lispo;

import org.homs.lispo.eval.Evaluator;
import org.homs.lispo.eval.Func;
import org.homs.lispo.parser.ast.Ast;
import org.homs.lispo.parser.ast.ListAst;
import org.homs.lispo.parser.ast.SymbolAst;
import org.homs.lispo.tokenizer.TokenAt;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CustomFunc implements Func {

    final Evaluator ev;
    final ListAst argDefs;
    final List<String> argNames;
    final List<Ast> bodies;

    public CustomFunc(final Evaluator ev, ListAst argDefs, List<Ast> bodies) {
        super();
        this.ev = ev;
        this.argDefs = argDefs;
        this.bodies = bodies;
        this.argNames = new ArrayList<>();
        for (Ast argDef : argDefs.values) {
            if (!(argDef instanceof SymbolAst)) {
                throw new RuntimeException(); // TODO
            }
            this.argNames.add(((SymbolAst) argDef).value);
        }
    }

    @Override
    public Object eval(TokenAt tokenAt, Evaluator ev__/* XXX */, List<Object> args) throws Throwable {

        Evaluator ev2 = new Evaluator(ev);

        /*
            fa binding de cada nom d'argument declarat, amb el valor que se li passa
            en la crida, en un nou context de {@link Evaluator}.
         */
        for (int i = 0; i < argNames.size(); i++) {
            String argName = argNames.get(i);

            final Object argValue;
            {
                if (argName.startsWith("..." )) {
                    argName = argName.substring("...".length());
                    List<Object> v = new ArrayList<>();
                    for (int j = i; j < args.size(); j++) {
                        v.add(args.get(j));
                    }
                    argValue = v;
                } else {
                    argValue = args.get(i);
                }
            }

            ev2.getEnvironment().def(argName, argValue);
        }

        /*
            evalua els bodies amb el nou context
         */
//        Evaluator ev2 = new Evaluator(ev__);
        Object r = null;
        for (Ast body : bodies) {
            r = ev2.evalAst(body);
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("(fn " );
        s.append(argDefs);
        s.append(" " );
        StringJoiner j = new StringJoiner(" " );
        for (Ast body : bodies) {
            j.add(body.toString());
        }
        s.append(j);
        s.append(")" );
        return s.toString();
    }
}