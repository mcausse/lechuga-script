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
            case '"':
                return parseStringAst();
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

    private Ast parseStringAst() {
        var position = lexer.getCurrentPosition();
        var s = new StringBuilder();
        lexer.consumeChars("\"");
        while (lexer.isNotEof() && lexer.getCurrentChar() != '"') {
            char c = lexer.getCurrentChar();
            if (c == '\\') {
                lexer.consumeChars("\\"); // chupa \
                s.append(getUnescapedChar(c));
            } else {
                s.append(c);
            }
            lexer.consumeChar();
        }
        lexer.consumeChars("\"");
        return new StringAst(position, s.toString());
    }
//    protected Token consumeString(String delimiter, EToken tokenType) {
//        Token r;
//        p += delimiter.length(); // chupa """
//        StringBuilder s = new StringBuilder();
//        int k = p;
//        while (k + delimiter.length() <= program.length() && !program.startsWith(delimiter, k)) {
//            if (program.charAt(k) == '\\' && k + 1 < program.length() /*&& program.charAt(k + 1) == '"'*/) {
//                k++; // chupa the \
//                char unescapedChar = getUnescapedChar(program.charAt(k));
//                s.append(unescapedChar);
//            } else {
//                s.append(program.charAt(k));
//            }
//            k++;
//        }
//        if (k + delimiter.length() > program.length()) {
//            throw new RuntimeException("expected closing " + delimiter + " but eof; opened at " + sourceDesc + ":" + row + ":" + col);
//        }
//        String value = s.toString();
//        p = k + delimiter.length(); // chupa """
//        r = new Token(tokenType, value, sourceDesc, row, col);
//        return r;
//    }

    private char getUnescapedChar(char escapedChar) {
        char unescapedChar;
        switch (escapedChar) {
            case 'n':
                unescapedChar = '\n';
                break;
            case 'r':
                unescapedChar = '\r';
                break;
            case 't':
                unescapedChar = '\t';
                break;
            default:
                unescapedChar = escapedChar;
        }
        return unescapedChar;
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
