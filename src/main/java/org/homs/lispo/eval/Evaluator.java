package org.homs.lispo.eval;

import org.homs.lispo.Environment;
import org.homs.lispo.parser.ast.*;
import org.homs.lispo.util.AssertionError;
import org.homs.lispo.util.ReflectUtils;
import org.homs.lispo.util.ValidationError;

import java.util.*;
import java.util.Map.Entry;

public class Evaluator {

    final Environment env;

    /**
     * funcions que no han d'evaluar tots els arguments: p.ex. if, and, ... Aquestes
     * funcions rebran els arguments sense evaluar, o sigui de tipus {@link Ast}.
     */
    final Collection<String> lazyFuncNames;

    public Evaluator(Environment env, Collection<String> lazyFuncNames) {
        super();
        this.env = env;
        this.lazyFuncNames = lazyFuncNames;
    }

    public Evaluator(Evaluator ev/*, Ast thisReference*/) {
        super();
        this.env = new Environment(ev.env);
        this.lazyFuncNames = ev.lazyFuncNames;
    }

    public Object evalAst(Ast ast) throws Throwable {

        try {
            if (ast instanceof InterpolationStringAst) {
                return evaluateStringInterpolation((InterpolationStringAst) ast);
            } else if (ast instanceof StringAst) {
                return ((StringAst) ast).value;
            } else if (ast instanceof NumberAst) {
                return ((NumberAst) ast).value;
            } else if (ast instanceof NullAst) {
                return null;
            } else if (ast instanceof BooleanAst) {
                return ((BooleanAst) ast).value;
            } else if (ast instanceof SymbolAst) {
                String sym = ((SymbolAst) ast).value;
                return env.get(sym);
            } else if (ast instanceof ListAst) {
                List<Ast> listAstValues = ((ListAst) ast).values;
                return evalListAst(listAstValues);
            } else if (ast instanceof MapAst) {
                Map<Ast, Ast> mapAstValues = ((MapAst) ast).values;
                return evalMapAst(mapAstValues);
            } else if (ast instanceof ParenthesisAst) {
                ParenthesisAst parenthesisAst = (ParenthesisAst) ast;

                Object op = evalAst(parenthesisAst.operator);
                if (op instanceof Func) {
                    final List<Object> args;
                    if (isLazyFunc(parenthesisAst.operator)) {
                        args = new ArrayList<>(parenthesisAst.arguments);
                    } else {
                        args = evalListAst(parenthesisAst.arguments);
                    }
                    Func f = (Func) evalAst(parenthesisAst.operator);
                    return f.eval(ast.getTokenAt(), this, args);
                } else {


                    List<Ast> args = parenthesisAst.arguments;

                    // TODO validate arguments?
                    String methodName = (String) evalAst(args.get(0));

                    var evaluatedArgs = new ArrayList<>();
                    for (int i = 1; i < args.size(); i++) {
                        var evaluatedArg = evalAst(args.get(i));
                        evaluatedArgs.add(evaluatedArg);
                    }
                    return ReflectUtils.callMethod(op, methodName, evaluatedArgs.toArray());
                }
            } else {
                throw new RuntimeException(ast.getClass().getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("error evaluating: " + ast.toString() + "; " + ast.getTokenAt().toString(), e);
        } catch (AssertionError e) {
            throw new AssertionError(e.getMessage(), new AssertionError(ast + "; " + ast.getTokenAt().toString()));
        } catch (ValidationError e) {
            throw new ValidationError(e.getMessage(), new ValidationError(ast + "; " + ast.getTokenAt().toString()));
        }
    }

    protected String evaluateStringInterpolation(InterpolationStringAst ast) throws Throwable {

        StringBuilder s = new StringBuilder();
        for (Ast astPart : ast.templateParts) {
            Object r = evalAst(astPart);
            s.append(r);
        }
        return s.toString();
    }

    protected boolean isLazyFunc(Ast operator) {
        return operator instanceof SymbolAst && lazyFuncNames.contains(((SymbolAst) operator).value);
    }

    protected List<Object> evalListAst(List<Ast> listAstValues) throws Throwable {
        List<Object> r = new ArrayList<>();

        // "this" feature
        Evaluator ev = new Evaluator(this);
        ev.getEnvironment().def("this", r);

        for (Ast a : listAstValues) {
            r.add(ev.evalAst(a));
        }
        return r;
    }

    protected Map<Object, Object> evalMapAst(Map<Ast, Ast> mapAstValues) throws Throwable {
        Map<Object, Object> r = new LinkedHashMap<>();

        // "this" feature
        Evaluator ev = new Evaluator(this);
        ev.getEnvironment().def("this", r);

        for (Entry<Ast, Ast> a : mapAstValues.entrySet()) {
            Object k = ev.evalAst(a.getKey());
            Object v = ev.evalAst(a.getValue());
            r.put(k, v);
        }
        return r;
    }

    public Environment getEnvironment() {
        return env;
    }
}
