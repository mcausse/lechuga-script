package org.homs.lechugascript2025.parser;


import org.homs.lechugascript2025.LechugaException;
import org.homs.lechugascript2025.lexer.Lexer;
import org.homs.lechugascript2025.parser.ast.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public List<Ast> parse() {
        List<Ast> r = new ArrayList<>();
        while (lexer.isNotEof()) {
            r.add(parseAst());
        }
        return r;
    }

    protected Ast parseAst() {
        lexer.consumeBlanks();
        switch (lexer.getCurrentChar()) {
            case '(':
                return parseFunCallAst();
            case '[':
                return parseListAst();
            case '{':
                return parseMapAst();
//            case '"':
//                return parseStringAst();
            case ':':
                return parseWordAst();
            default: {
                String tokenValue = lexer.getNextWordWithoutConsuming();
                if (tokenValue.isEmpty()) {
                    throw new LechugaException("unexpected: '" + lexer.getCurrentChar() + "'", lexer.getCurrentPosition());
                }
                if (tokenValue.equals("null")) {
                    return parseNull(tokenValue);
                } else if (tokenValue.equals("true") || tokenValue.equals("false")) {
                    return parseBoolean(tokenValue);
                }

                try {
                    return parseNumericAst(tokenValue);
                } catch (NumberFormatException nfe) {
                    // nothing to do.
                }

                return parseSymbolAst(tokenValue);
            }
        }
    }

    private Ast parseMapAst() {
        var position = lexer.getCurrentPosition();
        Map<Ast, Ast> map = new LinkedHashMap<>();
        lexer.consumeChars("{");
        lexer.consumeBlanks();
        while (lexer.isNotEof() && lexer.getCurrentChar() != '}') {
            Ast key = parseAst();
            lexer.consumeBlanks();
            Ast value = parseAst();
            lexer.consumeBlanks();

            map.put(key, value);
        }
        if (lexer.isEof()) {
            throw new LechugaException("expecting } but eof; ", position);
        }
        lexer.consumeChars("}");
        lexer.consumeBlanks();
        return new MapAst(position, map);
    }

    private Ast parseListAst() {
        var position = lexer.getCurrentPosition();
        List<Ast> asts = new ArrayList<>();
        lexer.consumeChars("[");
        lexer.consumeBlanks();
        while (lexer.isNotEof() && lexer.getCurrentChar() != ']') {
            asts.add(parseAst());
            lexer.consumeBlanks();
        }
        if (lexer.isEof()) {
            throw new LechugaException("expecting ] but eof; ", position);
        }
        lexer.consumeChars("]");
        lexer.consumeBlanks();
        return new ListAst(position, asts);
    }

    private FunCallAst parseFunCallAst() {
        var position = lexer.getCurrentPosition();
        List<Ast> asts = new ArrayList<>();
        lexer.consumeChars("(");
        lexer.consumeBlanks();
        while (lexer.isNotEof() && lexer.getCurrentChar() != ')') {
            asts.add(parseAst());
            lexer.consumeBlanks();
        }
        if (lexer.isEof()) {
            throw new LechugaException("expecting ) but eof; ", position);
        }
        lexer.consumeChars(")");
        return new FunCallAst(position, asts);
    }

    private WordAst parseWordAst() {
        var position = lexer.getCurrentPosition();
        lexer.consumeChars(":");
        String word = lexer.consumeWord();
        return new WordAst(position, word);
    }

    private Ast parseSymbolAst(String tokenValue) {
        var position = lexer.getCurrentPosition();
        lexer.consumeChars(tokenValue);
        return new SymbolAst(position, tokenValue);
    }

    private NumericAst parseNumericAst(String tokenValue) throws NumberFormatException {
        var position = lexer.getCurrentPosition();
        Number value;
        try {
            value = Integer.parseInt(tokenValue);
        } catch (NumberFormatException nfe) {
            value = Double.parseDouble(tokenValue);
        }
        lexer.consumeChars(tokenValue);
        return new NumericAst(position, value);
    }

    private BooleanAst parseBoolean(String tokenValue) {
        var position = lexer.getCurrentPosition();
        lexer.consumeChars(tokenValue);
        boolean value = Boolean.parseBoolean(tokenValue);
        return new BooleanAst(position, value);
    }

    private Ast parseNull(String tokenValue) {
        var position = lexer.getCurrentPosition();
        lexer.consumeChars(tokenValue);
        return new NullAst(position);
    }

}
