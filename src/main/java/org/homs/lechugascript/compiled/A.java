package org.homs.lechugascript.compiled;

public class A {

    static Object mul(Object... args) {
        return null;
    }

    public static void main(String[] argsss) {


        // (defn mul [x y] (* x y))
        Closure mul = A::mul;
        Closure mul2 = args -> null;

    }
}
