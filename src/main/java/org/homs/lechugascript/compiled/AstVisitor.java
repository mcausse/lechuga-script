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
        s.appendl("final var " + r + " = " + ast.value + ";");
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
        }

        switch (operator) {
            case "def":
                return visitDef(ast);
            case "fn":
                return visitFn(ast);
            case "to-double":
                return visitToDouble(ast);
            case "+":
                return visitAdd(ast);
        }

        s.appendl("// " + ast);
        String r = getNextVar();
        List<String> varArgumentNames = ast.arguments.stream().map(this::visit).collect(Collectors.toList());
        s.appendl("final var " + r + " = ((" + Closure.class.getName() + ") " + operator + ").apply(" + String.join(", ", varArgumentNames) + ");");
        return r;

    }

    private String visitAdd(ParenthesisAst ast) {
        List<String> varArgumentNames = ast.arguments.stream().map(this::visit).collect(Collectors.toList());
        String resultVarName = getNextVar();
        s.appendl("final var " + resultVarName + " = " + JRuntime.class.getName() + ".add(" + String.join(", ", varArgumentNames) + ");");
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

        s.appendl("var " + varName + " = " + resultVarName + ";");
        return varName;
    }

    private String visitToDouble(ParenthesisAst ast) {
        s.appendl("// " + ast.toString());
        Ast value = ast.arguments.get(0);
        String r = getNextVar();
        s.appendl("Double " + r + " = org.homs.lechugascript.compiled.JRuntime.toDouble(" + value + ");");
        return r;
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
