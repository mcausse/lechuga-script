package org.homs.lispo.util;

public class AssertionError extends Error {

    public AssertionError(String message) {
        super(message);
    }

    public AssertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
