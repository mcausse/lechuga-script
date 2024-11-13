package org.homs.lechugascript.compiled;

import org.homs.lechugascript.parser.ast.*;

import java.util.List;
import java.util.stream.Collectors;

public class AstVisitor {

    static class S {
        private final StringBuilder s = new StringBuilder();
        private int indentLevel = 0;

        public void incLevel() {
            this.indentLevel++;
        }

        public void decLevel() {
            this.indentLevel--;
        }

        public void appendl(String value) {
            this.s.append("    ".repeat(Math.max(0, this.indentLevel)));
            this.s.append(value + "\n");
        }

        @Override
        public String toString() {
            return s.toString();
        }
    }

    private final S s = new S();
    private int numVar = 0;

    public String getNextVar() {
        return "v__" + (this.numVar++);
    }


    public String visit(Ast ast) {
        if (ast instanceof NullAst) {
            return onVisitNull((NullAst) ast);
        } else if (ast instanceof BooleanAst) {
            return onVisitBoolean((BooleanAst) ast);
        } else if (ast instanceof NumberAst) {
            return onVisitNumber((NumberAst) ast);
        } else if (ast instanceof StringAst) {
            return onVisitString((StringAst) ast);
        } else if (ast instanceof SymbolAst) {
            return onVisitSymbol((SymbolAst) ast);
        } else if (ast instanceof ListAst) {
            return onVisitList((ListAst) ast);
        } else if (ast instanceof MapAst) {
            return onVisitMap((MapAst) ast);
        } else if (ast instanceof ParenthesisAst) {
            return onVisitParenthesis((ParenthesisAst) ast);
        }
        throw new RuntimeException("not recognized " + Ast.class.getName() + " subtype: " + ast.getClass().getName());
    }

    public String onVisitBoolean(BooleanAst ast) {
        String r = getNextVar();
        s.appendl("final var " + r + " = " + ast.value + ";");
        return r;
    }

    public String onVisitNull(NullAst ast) {
        String r = getNextVar();
        s.appendl("final Object " + r + " = null;");
        return r;
    }

    public String onVisitNumber(NumberAst ast) {
        String r = getNextVar();
        s.appendl("final var " + r + " = " + ast.value + ";");
        return r;
    }

    public String onVisitString(StringAst ast) {
        String r = getNextVar();
        s.appendl("final var " + r + " = \"" + ast.value + "\";");
        return r;
    }

    public String onVisitSymbol(SymbolAst ast) {
        String r = getNextVar();
        final String symbol = ast.value;
//        if(symbol.startsWith(".")) {
//            String invocation=symbol.substring(1);
////            s.appendl("final var " + r + " = " + symbol + ";");
//            s.appendl("final var " + r + " = (" + Closure.class.getName() + ") args_" + r + " -> {");
//            s.incLevel();
//            s.appendl("");
//            s.decLevel();
//            s.appendl("};");
//        }else {
        s.appendl("final var " + r + " = " + symbol + ";");
//        }
        return r;
    }

    public String onVisitList(ListAst ast) {
        s.appendl("// " + ast.toString());
        String r = getNextVar();
        s.appendl("var " + r + " = new java.util.ArrayList<>();");
        s.appendl("{");
        s.incLevel();
        for (var elementAst : ast.values) {
            s.appendl(r + ".add(" + visit(elementAst) + ");");
        }
        s.decLevel();
        s.appendl("}");
        return r;
    }

    public String onVisitMap(MapAst ast) {
        s.appendl("// " + ast.toString());
        return null;
    }

    public String onVisitParenthesis(ParenthesisAst ast) {
        final String operator;
        if (ast.operator instanceof ParenthesisAst) {
            operator = onVisitParenthesis((ParenthesisAst) ast.operator);
        } else {
            operator = ast.operator.toString();
            if (operator.startsWith(".")) {
                String invocation = operator.substring(1);
                String r = getNextVar();
                s.appendl("final var " + r + " = " + invocation + "(" +
                        ast.arguments.stream()
                                .map(this::visit)
                                .collect(Collectors.joining(", "))
                        + ");");
                return r;
            }
        }

        switch (operator) {
            case "def":
                return visitDef(ast);
            case "set":
                return visitSet(ast);
            case "fn":
                return visitFn(ast);
            case "if":
                return visitIf(ast);
            case "while":
                return visitWhile(ast);
            case "let":
                return visitLet(ast);

            case "+":
                return visitJRuntimeInvocation(ast, "add");
            case "-":
                return visitJRuntimeInvocation(ast, "sub");
            case "*":
                return visitJRuntimeInvocation(ast, "mul");
            case "/":
                return visitJRuntimeInvocation(ast, "div");
            case "%":
                return visitJRuntimeInvocation(ast, "mod");
            case "neg":
                return visitJRuntimeInvocation(ast, "neg");

            case "=":
                return visitJRuntimeInvocation(ast, "eq");
            case "<>":
                return visitJRuntimeInvocation(ast, "ne");
            case ">":
                return visitJRuntimeInvocation(ast, "gt");
            case "<":
                return visitJRuntimeInvocation(ast, "lt");
            case ">=":
                return visitJRuntimeInvocation(ast, "ge");
            case "<=":
                return visitJRuntimeInvocation(ast, "le");

            case "and":
                return visitAnd(ast);
            case "or":
                return visitOr(ast);
            case "not":
                return visitJRuntimeInvocation(ast, "not");

//            case "class":
//            case "field":
//            case "method":
//                return visitOOP(ast);
        }

        s.appendl("// " + ast);
        String r = getNextVar();
        List<String> varArgumentNames = ast.arguments.stream().map(this::visit).collect(Collectors.toList());
        s.appendl("final var " + r + " = ((" + Closure.class.getName() + ") " + operator + ").apply(" + String.join(", ", varArgumentNames) + ");");
        return r;
    }

//    private String visitOOP(ParenthesisAst ast) {
//        String operator = ast.operator.toString();
//        StringAst className = (StringAst) ast.arguments.get(0);
//        MapAst parameters = (MapAst) ast.arguments.get(1);
//        switch (operator) {
//            case "class":
//                s.appendl("public static class " + className + " {");
//
//                s.appendl("}");
//            case "field":
//            case "method":
//            default:
//                throw new RuntimeException(operator);
//        }
//    }

    private String visitLet(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        MapAst assigns = (MapAst) ast.arguments.get(0);
        List<Ast> bodies = ast.arguments.subList(1, ast.arguments.size());

        var resultVarName = getNextVar();
        s.appendl("Object " + resultVarName + " = null;");
        s.appendl("{");
        for (var assign : assigns.values.entrySet()) {
            String assignVarName = visit(assign.getValue());
            s.appendl("Object " + assign.getKey().toString() + " = " + assignVarName + ";");
        }
        for (var body : bodies) {
            s.appendl(resultVarName + " = " + visit(body) + ";");
        }
        s.appendl("}");

        return resultVarName;
    }

    private String visitIf(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        Ast condition = ast.arguments.get(0);
        Ast thenBody = ast.arguments.get(1);

        Ast elseBody = null;
        if (ast.arguments.size() > 2) {
            elseBody = ast.arguments.get(2);
        }

        var ifResultVarName = getNextVar();
        s.appendl("Object " + ifResultVarName + " = null;");

        var conditionResultVarName = visit(condition);
        s.appendl("if (" + conditionResultVarName + ") {");
        s.incLevel();
        var thenResultVarname = visit(thenBody);
        s.appendl(ifResultVarName + " = " + thenResultVarname + ";");
        s.decLevel();
        s.appendl("}");

        if (elseBody != null) {
            s.appendl("else {");
            s.incLevel();
            var elseResultVarname = visit(elseBody);
            s.appendl(ifResultVarName + " = " + elseResultVarname + ";");
            s.decLevel();
            s.appendl("}");
        }
        return ifResultVarName;
    }

    private String visitWhile(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        Ast condition = ast.arguments.get(0);
        List<Ast> thenBodies = ast.arguments.subList(1, ast.arguments.size());

        var ifResultVarName = getNextVar();
        s.appendl("Object " + ifResultVarName + " = null;");

        s.appendl("while (true) {");
        s.incLevel();
        var conditionResultVarName = visit(condition);
        s.appendl("if (!" + conditionResultVarName + ") {break;}");

        String thenResultVarname = null;
        for (var thenBody : thenBodies) {
            thenResultVarname = visit(thenBody);
        }
        s.appendl(ifResultVarName + " = " + thenResultVarname + ";");
        s.decLevel();
        s.appendl("}");

        return ifResultVarName;
    }

    private String visitJRuntimeInvocation(ParenthesisAst ast, String methodName) {
        s.appendl("// " + ast.toString());

        List<String> varArgumentNames = ast.arguments.stream().map(this::visit).collect(Collectors.toList());
        String resultVarName = getNextVar();
        s.appendl("final var " + resultVarName + " = " + JRuntime.class.getName() + "." + methodName + "(" + String.join(", ", varArgumentNames) + ");");
        return resultVarName;
    }

    private String visitAnd(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());

        String resultVarName = getNextVar();
        s.appendl("boolean " + resultVarName + " = false;");
        s.appendl("{");
        s.incLevel();
        for (int i = 0; i < ast.arguments.size(); i++) {
            Ast arg = ast.arguments.get(i);
            String argVarName = getNextVar();
            s.appendl("final var " + argVarName + " = " + JRuntime.class.getName() + ".toBoolean(" + visit(arg) + ");");
            s.appendl("if (" + argVarName + ") {");
            s.incLevel();
        }

        s.appendl(resultVarName + " = true;");

        for (int i = 0; i < ast.arguments.size(); i++) {
            s.decLevel();
            s.appendl("}");
        }

        s.decLevel();
        s.appendl("}");

        return resultVarName;
    }

    private String visitOr(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());

        String resultVarName = getNextVar();
        s.appendl("boolean " + resultVarName + " = false;");
        s.appendl("{");
        s.incLevel();
        for (int i = 0; i < ast.arguments.size(); i++) {
            Ast arg = ast.arguments.get(i);
            String argVarName = getNextVar();
            s.appendl("final boolean " + argVarName + " = " + JRuntime.class.getName() + ".toBoolean(" + visit(arg) + ");");
            s.appendl("if (" + argVarName + ") {");
            s.incLevel();
            s.appendl(resultVarName + " = true;");
            s.decLevel();
            s.appendl("} else {");
        }
        for (int i = 0; i < ast.arguments.size(); i++) {
            s.appendl("}");
            s.decLevel();
        }
        s.appendl("}");

        return resultVarName;
    }

    private String visitDef(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        String varName = ast.arguments.get(0).toString();

        List<Ast> bodies = ast.arguments.subList(1, ast.arguments.size());
        if (bodies.isEmpty()) {
            throw new RuntimeException();
        }

        String resultVarName = "null";
        for (var bodyAst : bodies) {
            resultVarName = visit(bodyAst);
        }

        s.appendl("Object " + varName + " = " + resultVarName + ";");
        return varName;
    }

    private String visitSet(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        String varName = ast.arguments.get(0).toString();

        List<Ast> bodies = ast.arguments.subList(1, ast.arguments.size());
        if (bodies.isEmpty()) {
            throw new RuntimeException();
        }

        String resultVarName = "null";
        for (var bodyAst : bodies) {
            resultVarName = visit(bodyAst);
        }

        s.appendl(varName + " = " + resultVarName + ";");
        return varName;
    }

    private String visitFn(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());

        if (ast.arguments.size() < 2) {
            throw new RuntimeException("args,bodies");
        }

        String functionName = getNextVar();
        s.appendl("final var " + functionName + " = (" + Closure.class.getName() + ") args_" + functionName + " -> {");
        s.incLevel();

        List<String> functionArgNames = ((ListAst) ast.arguments.get(0)).getValues().stream().map(Object::toString).collect(Collectors.toList());
        int i = 0;
        for (var functionArgName : functionArgNames) {
            s.appendl("final var " + functionArgName + " = args_" + functionName + "[" + i + "];");
            i++;
        }

        List<Ast> functionBodies = ast.arguments.subList(1, ast.arguments.size());
        String lastBodyResultVarName = null;
        for (Ast body : functionBodies) {
            lastBodyResultVarName = visit(body);
        }

        s.appendl("return " + lastBodyResultVarName + ";");

        s.decLevel();
        s.appendl("};");

        return functionName;
    }

    @Override
    public String toString() {
        return s.toString();
    }
}
