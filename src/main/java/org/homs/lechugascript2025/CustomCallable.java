package org.homs.lechugascript2025;

import org.homs.lechugascript.Environment;
import org.homs.lechugascript2025.parser.Ast;

import java.util.List;

public class CustomCallable implements Callable {

    final List<String> argNames;
    final List<Ast> bodies;
    final Environment closuredEnv;

    public CustomCallable(List<String> argNames, List<Ast> bodies, Environment envToClosure) {
        this.argNames = argNames;
        this.bodies = bodies;
        this.closuredEnv = envToClosure.copyFlatenized(); // TODO això fa closura
    }

    public CustomCallable(String fnName, List<String> argNames, List<Ast> bodies, Environment envToClosure) {
        this(argNames, bodies, envToClosure);

        this.closuredEnv.def(fnName, this);
    }

    @Override
    public Object evaluate(Environment ___env, List<Object> arguments) {
        Environment env3 = new Environment(closuredEnv);
        for (int i = 0; i < argNames.size(); i++) {
            String argName = argNames.get(i);
            Object argValue = arguments.get(i);
            env3.def(argName, argValue);
        }

        Object r = null;
        for (Ast body : bodies) {
            r = body.evaluate(env3);
        }

        return r;
    }
}
