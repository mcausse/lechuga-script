package org.homs.lechugascript.tokenizer;

import java.util.Iterator;

public class Tokenizer implements Iterator<Token> {

    final String program;
    final String sourceDesc;
    int row, col;
    int p;

    public Tokenizer(String program, String sourceDesc) {
        this.program = program;
        this.sourceDesc = sourceDesc;
        this.row = 1;
        this.col = 1;
        this.p = 0;
    }

    @Override
    public boolean hasNext() {
        consumeWhitespaces();
        return p < program.length();
    }

    @Override
    public Token next() {

        consumeWhitespaces();

        Token r;

        char c = program.charAt(p);
        int beginTokenPos = p;

        switch (c) {
            case '"': {
                if (program.startsWith("\"\"\"", p)) {
                    r = consumeString("\"\"\"", EToken.INTERPOLATION_STRING);
                } else {
                    r = consumeString("\"", EToken.STRING);
                }
                break;
            }
            case ':': {
                p++; // chupa :
                int k = p;
                while (k < program.length() && (program.charAt(k) == '.' || program.charAt(k) == '-' || program.charAt(k) == '/'
                        || Character.isJavaIdentifierPart(program.charAt(k)))) {
                    k++;
                }
                String value = program.substring(p, k);
                p = k;
                r = new Token(EToken.STRING, value, sourceDesc, row, col);
                break;
            }
            case '(':
                r = new Token(EToken.OPEN_PAR, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            case ')':
                r = new Token(EToken.CLOSE_PAR, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            case '[':
                r = new Token(EToken.OPEN_LIST, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            case ']':
                r = new Token(EToken.CLOSE_LIST, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            case '{':
                r = new Token(EToken.OPEN_MAP, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            case '}':
                r = new Token(EToken.CLOSE_MAP, String.valueOf(c), sourceDesc, row, col);
                p++; // chupa
                break;
            default: {
                int k = p;
                while (k < program.length() && !Character.isWhitespace(program.charAt(k))
                        && "()[]{}".indexOf(program.charAt(k)) < 0) {
                    k++;
                }
                String value = program.substring(p, k);
                p = k;

                if (value.equals("null")) {
                    r = new Token(EToken.NULL, null, sourceDesc, row, col);
                } else if (value.equals("true") || value.equals("false")) {
                    r = new Token(EToken.BOOL, value, sourceDesc, row, col);
                } else {

                    try {
                        Integer.valueOf(value);
                        r = new Token(EToken.NUMERIC, value, sourceDesc, row, col);
                    } catch (NumberFormatException e) {
                        try {
                            Double.valueOf(value);
                            r = new Token(EToken.NUMERIC, value, sourceDesc, row, col);
                        } catch (NumberFormatException e2) {
                            r = new Token(EToken.SYMBOL, value, sourceDesc, row, col);
                        }
                    }
                }
                break;
            }
        }

        for (int i = beginTokenPos; i < p; i++) {
            updateRowCol(i);
        }
        return r;
    }

    protected Token consumeString(String delimiter, EToken tokenType) {
        Token r;
        p += delimiter.length(); // chupa """
        StringBuilder s = new StringBuilder();
        int k = p;
        while (k + delimiter.length() <= program.length() && !program.startsWith(delimiter, k)) {
            if (program.charAt(k) == '\\' && k + 1 < program.length() /*&& program.charAt(k + 1) == '"'*/) {
                k++; // chupa the \
                char unescapedChar = getUnescapedChar(program.charAt(k));
                s.append(unescapedChar);
            } else {
                s.append(program.charAt(k));
            }
            k++;
        }
        if (k + delimiter.length() > program.length()) {
            throw new RuntimeException("expected closing " + delimiter + " but eof; opened at " + sourceDesc + ":" + row + ":" + col);
        }
        String value = s.toString();
        p = k + delimiter.length(); // chupa """
        r = new Token(tokenType, value, sourceDesc, row, col);
        return r;
    }

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

    protected void consumeWhitespaces() {
        while (p < program.length() && (Character.isWhitespace(program.charAt(p)) || program.charAt(p) == ';')) {

            // chupa comentaris ";" ... \n
            if (program.charAt(p) == ';') {
                while (p < program.length() && program.charAt(p) != '\n') {
                    updateRowCol(p);
                    p++;
                }
                if (p >= program.length()) {
                    return;
                }
            }
            updateRowCol(p);
            p++;
        }
    }

    protected void updateRowCol(int i) {
        if (program.charAt(i) == '\n') {
            row++;
            col = 1;
        } else {
            col++;
        }
    }

}
