package org.homs.lispo.parser;

import org.homs.lispo.parser.ast.*;
import org.homs.lispo.tokenizer.EToken;
import org.homs.lispo.tokenizer.Token;
import org.homs.lispo.tokenizer.Tokenizer;

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
                return new BooleanAst(t, Boolean.valueOf(t.value));
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

    protected ParenthesisAst parseParenthesisAst(Token t) {

        if (!tokenizer.hasNext()) {
            throw new RuntimeException("expected ) but EOF");
        }
        Token operand = tokenizer.next();
        Ast operator = parseToken(operand);

        List<Ast> arguments = new ArrayList<>();
        Token targ = t;
        while (tokenizer.hasNext()) {
            targ = tokenizer.next();
            if (targ.type == EToken.CLOSE_PAR) {
                break;
            }
            Ast v = parseToken(targ);
            arguments.add(v);
        }

        if (targ.type != EToken.CLOSE_PAR) {
            throw new RuntimeException("expected ) but EOF; " + t.tokenAt);
        }

        return new ParenthesisAst(t, operator, arguments);
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
