package org.homs.lechugascript;

import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {

    final Environment parent;
    final Map<String, Object> variables;

    public Environment(Environment parent) {
        super();
        this.parent = parent;
        this.variables = new LinkedHashMap<>();
    }

    public Environment find(String sym) {
        Environment e = this;
        while (e != null) {
            if (e.variables.containsKey(sym)) {
                return e;
            }
            e = e.parent;
        }
        return null;
    }

    public void def(String sym, Object value) {
//        if (this.variables.containsKey(sym)) {
//            throw new RuntimeException("yet defined variable: '" + sym + "'; use set");
//        }
        this.variables.put(sym, value);
    }

    public void set(String sym, Object value) {
        Environment e = find(sym);
        if (e == null) {
            throw new RuntimeException("undefined variable for 'set' operation: " + sym);
        } else {
            e.variables.put(sym, value);
        }
    }

    public Object get(String sym) {
        Environment e = find(sym);
        if (e == null) {
            throw new RuntimeException("variable not defined: '" + sym + "'");
        }
        return e.variables.get(sym);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        if (parent == null) {
            return variables.toString();
        } else {
            return parent + " => " + variables;
        }
    }

}
