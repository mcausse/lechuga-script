package org.homs.lispo.eval;

import org.homs.lispo.Environment;
import org.homs.lispo.parser.ast.*;
import org.homs.lispo.util.ReflectUtils;

import java.util.*;
import java.util.Map.Entry;
import org.homs.lispo.util.AssertionError;

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

    public Evaluator(Evaluator ev) {
        super();
        this.env = new Environment(ev.env);
        this.lazyFuncNames = ev.lazyFuncNames;
    }

    public Object evalAst(Ast ast) throws Throwable {

        try {
            if (ast instanceof StringAst) {
                return ((StringAst) ast).value;
            } else if (ast instanceof NumberAst) {
                return ((NumberAst) ast).value;
            } else if (ast instanceof NullAst) {
                return null;
            } else if (ast instanceof BooleanAst) {
                return ((BooleanAst) ast).value;
            } else if (ast instanceof SymbolAst) {
                String sym = ((SymbolAst) ast).value;

                // TODO return env.get(sym);
                return evaluateSymbolInDotNotation(env, sym);

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

                    // TODO validate arguments
                    String methodName = (String) evalAst(args.get(0));
                    final List<Object> methodArgs;
                    if (args.size() == 1) {
                        methodArgs = Collections.emptyList();
                    } else {
                        methodArgs = (List<Object>) evalAst(args.get(1));
                    }
                    return ReflectUtils.callMethod(op, methodName, methodArgs.toArray());

//					if (op instanceof Boolean) {
//
//						if (args.size() == 1) {
//
//							// when
//							Ast thenAst = (Ast) args.get(0);
//							if (((Boolean) op)) {
//								Evaluator ev2 = new Evaluator(new Environment(getEnv()), getLazyFuncNames());
//								return ev2.evalAst(thenAst);
//							}
//							return null;
//
//						} else if (args.size() == 2) {
//
//							// if-else
//							Ast thenAst = (Ast) args.get(0);
//							Ast elseAst = (Ast) args.get(1);
//							Evaluator ev2 = new Evaluator(new Environment(getEnv()), getLazyFuncNames());
//							if (((Boolean) op)) {
//								return ev2.evalAst(thenAst);
//							} else {
//								return ev2.evalAst(elseAst);
//							}
//
//						} else {
//							throw new RuntimeException(); // TODO
//						}
//					} else if (op instanceof String) {
//
//						if (args.size() == 1) {
//							Integer index = ((Number) evalAst(args.get(0))).intValue();
//							return String.valueOf(((String) op).charAt(index));
//						} else if (args.size() == 2) {
//							Integer index1 = ((Number) evalAst(args.get(0))).intValue();
//							Integer index2 = ((Number) evalAst(args.get(1))).intValue();
//							return ((String) op).substring(index1, index2);
//						} else {
//							throw new RuntimeException(); // TODO
//						}
//					} else
//                    if (op instanceof List) {
//                        if (args.size() == 1) {
//                            Integer index = ((Number) evalAst(args.get(0))).intValue();
//                            return ((List<?>) op).get(index);
//                        } else if (args.size() == 2) {
//                            Integer index1 = ((Number) evalAst(args.get(0))).intValue();
//                            Integer index2 = ((Number) evalAst(args.get(1))).intValue();
//                            return ((List<?>) op).subList(index1, index2);
//                        } else {
//                            throw new RuntimeException(); // TODO
//                        }
//                    } else if (op instanceof Map) {
//                        if (args.size() == 1) {
//                            Object key = evalAst(args.get(0));
//                            return ((Map<?, ?>) op).get(key);
//                        } else {
//                            throw new RuntimeException(); // TODO
//                        }
//                    } else {
//                        // call method to java object
////						if (args.size() == 2) {
////							String methodName = (String) evalAst(args.get(0));
////							List<?> methodArgs = (List<?>) evalAst(args.get(1));
////							return ReflectUtils.callMethod(op, methodName, methodArgs.toArray());
////						} else {
//                        throw new RuntimeException(); // TODO
////						}
//                    }
                    // throw new RuntimeException(); // TODO
                }
            } else {
                throw new RuntimeException(ast.getClass().getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("error evaluating: " + ast.toString() + "; " + ast.getTokenAt().toString(), e);
        } catch (AssertionError e) {
            throw new AssertionError(e.getMessage(),  new AssertionError(ast.toString() + "; " + ast.getTokenAt().toString()));
        }
    }

    protected Object evaluateSymbolInDotNotation(Environment env, String sym) {
        String[] parts = sym.split("\\.");
        Object target = env.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            target = ReflectUtils.get(target, parts[i]);
        }
        return target;
    }

    protected boolean isLazyFunc(Ast operator) {
        return operator instanceof SymbolAst && lazyFuncNames.contains(((SymbolAst) operator).value);
    }

    protected List<Object> evalListAst(List<Ast> listAstValues) throws Throwable {
        List<Object> r = new ArrayList<>();
        for (Ast a : listAstValues) {
            r.add(evalAst(a));
        }
        return r;
    }

    protected Map<Object, Object> evalMapAst(Map<Ast, Ast> mapAstValues) throws Throwable {
        Map<Object, Object> r = new LinkedHashMap<>();
        for (Entry<Ast, Ast> a : mapAstValues.entrySet()) {
            Object k = evalAst(a.getKey());
            Object v = evalAst(a.getValue());
            r.put(k, v);
        }
        return r;
    }

    public Environment getEnvironment() {
        return env;
    }
}
