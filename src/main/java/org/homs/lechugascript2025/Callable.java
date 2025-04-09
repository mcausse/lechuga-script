package org.homs.lechugascript2025;

import org.homs.lechugascript.Environment;

import java.util.List;

public interface Callable {

    Object evaluate(Environment env, List<Object> arguments);
}