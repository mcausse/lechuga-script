package org.homs.lechugascript2025.lexer;


import org.homs.lechugascript2025.LechugaException;

public class Lexer {

    final String scriptUrn;
    int row;
    int col;

    final String content;
    int p;

    public Lexer(String scriptUrn, String content) {
        this.scriptUrn = scriptUrn;
        this.content = content;
        this.p = 0;
        this.row = 1;
        this.col = 1;
    }

    public Position getCurrentPosition() {
        return new Position(scriptUrn, row, col);
    }

    public boolean isNotEof() {
        return p < content.length();
    }

    public boolean isEof() {
        return !isNotEof();
    }

    public boolean currentPosStartsWith(String prefix) {
        return isNotEof() && content.startsWith(prefix, p);
    }

    public boolean currentPosStartsWithBlank() {
        return isNotEof() && Character.isWhitespace(content.charAt(p));
    }

    public void consumeChar() {
        if (content.charAt(p) == '\n') {
            row++;
            col = 1;
        } else {
            col++;
        }
        this.p++;
    }

    public void consumeBlanks() {
        while (isNotEof() && Character.isWhitespace(content.charAt(p))) {
            consumeChar();
        }
    }

    public String consumeWord() {
        int initialP = p;
        while (isNotEof() && isWordChar(content.charAt(p))) {
            consumeChar();
        }
        var r = getString(initialP);
        if (r.isEmpty()) {
            throw new LechugaException("expected to consume a word, but not; at: ", getCurrentPosition());
        }
        return r;
    }

    protected boolean isWordChar(char c) {
        return Character.isJavaIdentifierPart(c) || c == '-' || c == '/';
    }

    public void consumeChars(String prefix) {
        if (p + prefix.length() > content.length()) {
            throw new LechugaException("expected: " + prefix + ", but eof; at: ", getCurrentPosition());
        }
        if (!currentPosStartsWith(prefix)) {
            throw new LechugaException("expected: " + prefix + ", at: ", getCurrentPosition());
        }
        for (int i = 0; i < prefix.length(); i++) {
            consumeChar();
        }
    }

    public String getString(int textStartPos) {
        return this.content.substring(textStartPos, p);
    }

    public char getCurrentChar() {
        return content.charAt(p);
    }

    public String getNextWordWithoutConsuming() {
        int k = p;
        while (k < content.length() && !Character.isWhitespace(content.charAt(k))
                && "()[]{}".indexOf(content.charAt(k)) < 0) {
            k++;
        }
        String value = content.substring(p, k);
        return value;
    }
}