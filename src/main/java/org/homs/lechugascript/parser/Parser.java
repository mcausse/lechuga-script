package org.homs.lechugascript.parser;

import org.homs.lechugascript.parser.ast.*;
import org.homs.lechugascript.tokenizer.EToken;
import org.homs.lechugascript.tokenizer.Token;
import org.homs.lechugascript.tokenizer.Tokenizer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    final Iterator<Token> tokenizer;

    public Parser(Iterator<Token> tokenizer) {
        super();
        this.tokenizer = tokenizer;
    }

    public List<Ast> parse() {
        List<Ast> r = new ArrayList<>();
        while (tokenizer.hasNext()) {
            Token t = tokenizer.next();
            Ast ast = parseToken(t);
            r.add(ast);
        }
        return r;
    }

    protected Ast parseToken(Token t) {

        switch (t.type) {
            case NUMERIC:
                try {
                    return new NumberAst(t, Integer.valueOf(t.value));
                } catch (NumberFormatException e) {
                    return new NumberAst(t, Double.valueOf(t.value));
                }
            case INTERPOLATION_STRING:
                return parseInterpolationStringAst(t);
            case STRING:
                return new StringAst(t, t.value);
            case NULL:
                return new NullAst(t);
            case BOOL:
                return new BooleanAst(t, Boolean.parseBoolean(t.value));
            case SYMBOL:
                return new SymbolAst(t, t.value);
            case OPEN_PAR:
                return parseParenthesisAst(t);
            case OPEN_LIST:
                return parseListAst(t);
            case OPEN_MAP:
                return parseMapAst(t);
            default:
                throw new RuntimeException("unexpected token: '" + t + "', at: " + t.tokenAt);
        }
    }

    final protected Pattern stringInterpolationExpression = Pattern.compile("\\$\\{(.*?)\\}\\$");

    protected InterpolationStringAst parseInterpolationStringAst(Token t) {
        String string = t.value;
        List<Ast> templateParts = new ArrayList<>();

        Matcher m = stringInterpolationExpression.matcher(string);
        int pos = 0;
        while (m.find()) {
            String prev = string.substring(pos, m.start());
            templateParts.add(new StringAst(t, prev));

            String expression = m.group(1);

            Tokenizer tokenizer = new Tokenizer(expression, t.tokenAt.toString());
            Parser parser = new Parser(tokenizer);
            templateParts.addAll(parser.parse());

            pos = m.end();
        }
        String last = string.substring(pos);
        templateParts.add(new StringAst(t, last));

        return new InterpolationStringAst(t, string, templateParts);
    }

    // {[:one 1] [:two 2]}
    protected MapAst parseMapAst(Token t) {
        Map<Ast, Ast> r = new LinkedHashMap<>();
        Token t2 = t;
        while (tokenizer.hasNext()) {
            t2 = tokenizer.next();
            if (t2.type == EToken.CLOSE_MAP) {
                break;
            }
            ListAst l = parseListAst(t2);
            Ast k = l.getValues().get(0);
            Ast v = l.getValues().get(1);
            r.put(k, v);
        }

        if (t2.type != EToken.CLOSE_MAP) {
            throw new RuntimeException("expected } but EOF; " + t.tokenAt);
        }

        return new MapAst(t, r);
    }

    boolean isLambdaExpansionSymbol(Ast ast) {
        return (ast instanceof SymbolAst) && "=>".equals(((SymbolAst) ast).value);
    }

    protected ParenthesisAst parseParenthesisAst(Token t) {

        if (!tokenizer.hasNext()) {
            throw new RuntimeException("expected ) but EOF");
        }

        // the position of "=>" expansion symbol, if exists
        int lambdaOperatorPos = -1;

        List<Ast> parenthesisContent = new ArrayList<>();
        Token nextToken = t;
        while (tokenizer.hasNext()) {
            nextToken = tokenizer.next();
            if (nextToken.type == EToken.CLOSE_PAR) {
                break;
            }
            Ast ast = parseToken(nextToken);

            if (isLambdaExpansionSymbol(ast)) {
                lambdaOperatorPos = parenthesisContent.size();
            }

            parenthesisContent.add(ast);
        }
        if (nextToken.type != EToken.CLOSE_PAR) {
            throw new RuntimeException("expected ) but EOF; " + t.tokenAt);
        }


        if (lambdaOperatorPos >= 0) {

            //
            // LAMBDA EXPANSION
            //
            // converts the lambda expression (a b => (* a b)) to
            // the expanded (fn [a b] (* a b))
            //
            ListAst fnArgsList = new ListAst(t, parenthesisContent.subList(0, lambdaOperatorPos));

            Ast fnOperator = new SymbolAst(t, "fn");
            List<Ast> fnArgs = new ArrayList<>();
            fnArgs.add(fnArgsList);
            fnArgs.addAll(parenthesisContent.subList(lambdaOperatorPos + 1, parenthesisContent.size()));

            return new ParenthesisAst(t, fnOperator, fnArgs);
        } else {
            Ast operator = parenthesisContent.get(0);
            List<Ast> arguments = parenthesisContent.subList(1, parenthesisContent.size());
            return new ParenthesisAst(t, operator, arguments);
        }
    }

    protected ListAst parseListAst(Token t) {
        List<Ast> values = new ArrayList<>();
        Token t2 = t;
        while (tokenizer.hasNext()) {
            t2 = tokenizer.next();
            if (t2.type == EToken.CLOSE_LIST) {
                break;
            }
            Ast v = parseToken(t2);
            values.add(v);
        }

        if (t2.type != EToken.CLOSE_LIST) {
            throw new RuntimeException("expected ] but EOF; " + t.tokenAt);
        }

        return new ListAst(t, values);
    }

}
