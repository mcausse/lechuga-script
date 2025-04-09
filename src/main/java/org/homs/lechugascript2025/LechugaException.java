package org.homs.lechugascript2025;

import org.homs.lechugascript2025.lexer.Position;

public class LechugaException extends RuntimeException {

    final Position position;

    public LechugaException(String message, Position position) {
        super(message + "; at " + position.toString());
        this.position = position;
    }

    public LechugaException(String message, Position position, Throwable cause) {
        super(message + "; at " + position.toString(), cause);
        this.position = position;
    }
}
